package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.loraforwarder.rudzl.dto.RudzlMessage;
import nl.bertriksikken.loraforwarder.ttnulm.PayloadParseException;
import nl.bertriksikken.loraforwarder.ttnulm.SdsDhtCayenneMessage;
import nl.bertriksikken.loraforwarder.ttnulm.TtnUlmMessage;
import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.luftdaten.dto.LuftdatenItem;
import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.dto.TtnUplinkMessage;

public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.properties";
    private static final String SOFTWARE_VERSION = "https://github.com/bertrik/LoraLuftdatenForwarder";

    private final MqttListener mqttListener;
    private final LuftdatenUploader uploader;
    private final ExecutorService executor;
    private final EPayloadEncoding encoding;

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure("log4j.properties");

        ILoraForwarderConfig config = readConfig(new File(CONFIG_FILE));
        LoraLuftdatenForwarder app = new LoraLuftdatenForwarder(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private LoraLuftdatenForwarder(ILoraForwarderConfig config) {
        ILuftdatenApi restClient = LuftdatenUploader.newRestClient(config.getLuftdatenUrl(),
                config.getLuftdatenTimeout());
        uploader = new LuftdatenUploader(restClient);
        executor = Executors.newSingleThreadExecutor();
        encoding = EPayloadEncoding.fromId(config.getEncoding());

        mqttListener = new MqttListener(this::messageReceived, config.getMqttUrl(), config.getMqttAppId(),
                config.getMqttAppKey());

        LOG.info("Created new Luftdaten forwarder for encoding {}", encoding);
    }

    private void messageReceived(Instant instant, String topic, String message) {
        LOG.info("Received: '{}'", message);

        // decode JSON
        ObjectMapper mapper = new ObjectMapper();
        TtnUplinkMessage uplink;
        try {
            uplink = mapper.readValue(message, TtnUplinkMessage.class);
        } catch (IOException e) {
            LOG.warn("Could not parse JSON: '{}'", message);
            return;
        }
        String sensorId = String.format(Locale.ROOT, "TTN-%s", uplink.getHardwareSerial());

        SensorData sensorData = decodeTtnMessage(instant, sensorId, uplink);

        // schedule upload
        if (sensorData != null) {
            executor.execute(() -> handleMessageTask(sensorId, sensorData));
        }
    }

    // package-private to allow testing
    SensorData decodeTtnMessage(Instant instant, String sensorId, TtnUplinkMessage uplinkMessage) {
        SensorData sensorData = new SensorData();

        switch (encoding) {
        case RUDZL:
            RudzlMessage rudzlMessage = new RudzlMessage(uplinkMessage.getPayloadFields());
            sensorData.addValue(ESensorItem.PM10, rudzlMessage.getPM10());
            sensorData.addValue(ESensorItem.PM2_5, rudzlMessage.getPM2_5());
            sensorData.addValue(ESensorItem.HUMI, rudzlMessage.getRH());
            sensorData.addValue(ESensorItem.TEMP, rudzlMessage.getT());
            sensorData.addValue(ESensorItem.PRESSURE, rudzlMessage.getP() * 100.0); // mBar to Pa
            break;
        case TTN_ULM:
            TtnUlmMessage ulmMessage = new TtnUlmMessage();
            try {
                ulmMessage.parse(uplinkMessage.getRawPayload());
            } catch (PayloadParseException e) {
                LOG.warn("Could not parse raw payload");
                break;
            }
            sensorData.addValue(ESensorItem.PM10, ulmMessage.getPm10());
            sensorData.addValue(ESensorItem.PM2_5, ulmMessage.getPm2_5());
            sensorData.addValue(ESensorItem.HUMI, ulmMessage.getRhPerc());
            sensorData.addValue(ESensorItem.TEMP, ulmMessage.getTempC());
            break;
        case CAYENNE_SDS_DHT:
            SdsDhtCayenneMessage cayenne = new SdsDhtCayenneMessage();
            try {
                cayenne.parse(uplinkMessage.getRawPayload());
            } catch (PayloadParseException e) {
                LOG.warn("Could not parse raw payload");
                break;
            }
            if (cayenne.hasPm10()) {
                sensorData.addValue(ESensorItem.PM10, cayenne.getPm10());
            }
            if (cayenne.hasPm2_5()) {
                sensorData.addValue(ESensorItem.PM2_5, cayenne.getPm2_5());
            }
            if (cayenne.hasRhPerc()) {
                sensorData.addValue(ESensorItem.HUMI, cayenne.getRhPerc());
            }
            if (cayenne.hasTempC()) {
                sensorData.addValue(ESensorItem.TEMP, cayenne.getTempC());
            }
            break;
        default:
            throw new IllegalStateException("Unhandled encoding: " + encoding);
        }
        return sensorData;
    }

    private void handleMessageTask(String sensorId, SensorData data) {
        // forward to luftdaten, in an exception safe manner
        try {
            // pin 1 (dust sensors)
            if (data.hasValue(ESensorItem.PM10) || data.hasValue(ESensorItem.PM2_5)
                    || data.hasValue(ESensorItem.PM1_0)) {
                LuftdatenMessage p1Message = new LuftdatenMessage(SOFTWARE_VERSION);
                if (data.hasValue(ESensorItem.PM10)) {
                    p1Message.addItem(new LuftdatenItem("P1", data.getValue(ESensorItem.PM10)));
                }
                if (data.hasValue(ESensorItem.PM2_5)) {
                    p1Message.addItem(new LuftdatenItem("P2", data.getValue(ESensorItem.PM2_5)));
                }
                if (data.hasValue(ESensorItem.PM1_0)) {
                    p1Message.addItem(new LuftdatenItem("P0", data.getValue(ESensorItem.PM1_0)));
                }
                uploader.uploadMeasurement(sensorId, "1", p1Message);
            }

            // pin 3: temperature & pressure, but no humidity
            if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.PRESSURE)
                    && !data.hasValue(ESensorItem.HUMI)) {
                LuftdatenMessage p3Message = new LuftdatenMessage(SOFTWARE_VERSION);
                p3Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
                p3Message.addItem(new LuftdatenItem("pressure", data.getValue(ESensorItem.PRESSURE)));
                uploader.uploadMeasurement(sensorId, "3", p3Message);
            }

            // pin 7: temperature & humidity, but no pressure
            if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI)
                    && !data.hasValue(ESensorItem.PRESSURE)) {
                LuftdatenMessage p7Message = new LuftdatenMessage(SOFTWARE_VERSION);
                p7Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
                p7Message.addItem(new LuftdatenItem("humidity", data.getValue(ESensorItem.HUMI)));
                uploader.uploadMeasurement(sensorId, "7", p7Message);
            }

            // pin 9: position
            // not implemented yet
            
            // pin 11: temperature & humidity & pressure
            if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI)
                    && data.hasValue(ESensorItem.PRESSURE)) {
                LuftdatenMessage p11Message = new LuftdatenMessage(SOFTWARE_VERSION);
                p11Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
                p11Message.addItem(new LuftdatenItem("humidity", data.getValue(ESensorItem.TEMP)));
                p11Message.addItem(new LuftdatenItem("pressure", data.getValue(ESensorItem.HUMI)));
                uploader.uploadMeasurement(sensorId, "11", p11Message);
            }

            // pin 13: only temperature
            if (data.hasValue(ESensorItem.TEMP) && !data.hasValue(ESensorItem.HUMI)
                    && !data.hasValue(ESensorItem.PRESSURE)) {
                LuftdatenMessage p13Message = new LuftdatenMessage(SOFTWARE_VERSION);
                p13Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
                uploader.uploadMeasurement(sensorId, "13", p13Message);
            }
            
            // pin 15: sound not implemented
            // pin 17: NO2 not implemented
            // pin 19: radiation not implemented
        } catch (Exception e) {
            LOG.trace("Caught exception", e);
            LOG.warn("Caught exception: {}", e.getMessage());
        }
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting LoraLuftdatenForwarder application");

        // start sub-modules
        uploader.start();
        mqttListener.start();

        LOG.info("Started LoraLuftdatenForwarder application");
    }

    /**
     * Stops the application.
     * 
     * @throws MqttException
     */
    private void stop() {
        LOG.info("Stopping LoraLuftdatenForwarder application");

        mqttListener.stop();
        executor.shutdown();
        uploader.stop();

        LOG.info("Stopped LoraLuftdatenForwarder application");
    }

    private static ILoraForwarderConfig readConfig(File file) throws IOException {
        final LoraForwarderConfig config = new LoraForwarderConfig();
        try (FileInputStream fis = new FileInputStream(file)) {
            config.load(fis);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                config.save(fos);
            }
        }
        return config;
    }

}
