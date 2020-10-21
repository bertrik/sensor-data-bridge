package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.loraforwarder.rudzl.dto.RudzlMessage;
import nl.bertriksikken.loraforwarder.ttnulm.PayloadParseException;
import nl.bertriksikken.loraforwarder.ttnulm.TtnCayenneMessage;
import nl.bertriksikken.loraforwarder.ttnulm.TtnUlmMessage;
import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenConfig;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.opensense.IOpenSenseRestApi;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.opensense.OpenSenseUploader;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnConfig;
import nl.bertriksikken.ttn.dto.TtnUplinkMessage;
import nl.bertriksikken.ttn.dto.TtnUplinkMetaData.TtnUplinkGateway;

public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.yaml";

    private final List<MqttListener> mqttListeners = new ArrayList<>();
    private final LuftdatenUploader luftdatenUploader;
    private final OpenSenseUploader openSenseUploader;

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure("log4j.properties");

        LoraForwarderConfig config = readConfig(new File(CONFIG_FILE));
        LoraLuftdatenForwarder app = new LoraLuftdatenForwarder(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private LoraLuftdatenForwarder(LoraForwarderConfig config) {
        LuftdatenConfig luftdatenConfig = config.getLuftdatenConfig();
        ILuftdatenApi restClient = LuftdatenUploader.newRestClient(luftdatenConfig.getUrl(),
                Duration.ofSeconds(luftdatenConfig.getTimeout()));
        luftdatenUploader = new LuftdatenUploader(restClient);

        OpenSenseConfig openSenseConfig = config.getOpenSenseConfig();
        IOpenSenseRestApi openSenseClient = OpenSenseUploader.newRestClient(openSenseConfig.getUrl(),
                Duration.ofSeconds(openSenseConfig.getTimeoutSec()));
        openSenseUploader = new OpenSenseUploader(config.getOpenSenseConfig().getIds(), openSenseClient);

        TtnConfig ttnConfig = config.getTtnConfig();
        for (TtnAppConfig ttnAppConfig : config.getTtnConfig().getApps()) {
            EPayloadEncoding encoding = ttnAppConfig.getEncoding();
            LOG.info("Adding MQTT listener for TTN application '{}' with encoding '{}'", ttnAppConfig.getName(),
                    encoding);
            MqttListener listener = new MqttListener((topic, message) -> messageReceived(encoding, topic, message),
                    ttnConfig.getUrl(), ttnAppConfig.getName(), ttnAppConfig.getKey());
            mqttListeners.add(listener);
        }
    }

    private void messageReceived(EPayloadEncoding encoding, String topic, String message) {
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
        // decode and upload
        try {
            SensorData sensorData = decodeTtnMessage(encoding, uplink);
            luftdatenUploader.scheduleUpload(uplink.getHardwareSerial(), sensorData);
            openSenseUploader.scheduleUpload(uplink.getHardwareSerial(), sensorData);
        } catch (PayloadParseException e) {
            LOG.warn("Could not parse payload from: '{}", uplink);
        }
    }

    // package-private to allow testing
    SensorData decodeTtnMessage(EPayloadEncoding encoding, TtnUplinkMessage uplinkMessage)
            throws PayloadParseException {
        SensorData sensorData = new SensorData();

        // add RSSI if present
        OptionalDouble bestRssi = uplinkMessage.getMetaData().getGateways().stream()
                .mapToDouble(TtnUplinkGateway::getRssi).max();
        if (bestRssi.isPresent()) {
            double rssi = bestRssi.getAsDouble();
            if (Double.isFinite(rssi)) {
                sensorData.addValue(ESensorItem.RSSI, rssi);
            }
        }

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
            ulmMessage.parse(uplinkMessage.getRawPayload());
            sensorData.addValue(ESensorItem.PM10, ulmMessage.getPm10());
            sensorData.addValue(ESensorItem.PM2_5, ulmMessage.getPm2_5());
            sensorData.addValue(ESensorItem.HUMI, ulmMessage.getRhPerc());
            sensorData.addValue(ESensorItem.TEMP, ulmMessage.getTempC());
            break;
        case CAYENNE:
            TtnCayenneMessage cayenne = new TtnCayenneMessage();
            cayenne.parse(uplinkMessage.getRawPayload());
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
            if (cayenne.hasPressureMillibar()) {
                sensorData.addValue(ESensorItem.PRESSURE, 100.0 * cayenne.getPressureMillibar());
            }
            if (cayenne.hasPosition()) {
                double[] position = cayenne.getPosition();
                sensorData.addValue(ESensorItem.POS_LAT, position[0]);
                sensorData.addValue(ESensorItem.POS_LON, position[1]);
                sensorData.addValue(ESensorItem.POS_ALT, position[2]);
            }
            break;
        default:
            throw new IllegalStateException("Unhandled encoding: " + encoding);
        }
        return sensorData;
    }

    private Double getRssi(TtnUplinkMessage uplinkMessage) {
        return uplinkMessage.getMetaData().getGateways().stream().mapToDouble(TtnUplinkGateway::getRssi).max()
                .getAsDouble();
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting LoraLuftdatenForwarder application");

        // start sub-modules
        luftdatenUploader.start();
        openSenseUploader.start();
        for (MqttListener listener : mqttListeners) {
            listener.start();
        }

        LOG.info("Started LoraLuftdatenForwarder application");
    }

    /**
     * Stops the application.
     * 
     * @throws MqttException
     */
    private void stop() {
        LOG.info("Stopping LoraLuftdatenForwarder application");

        mqttListeners.forEach(MqttListener::stop);
        openSenseUploader.stop();
        luftdatenUploader.stop();

        LOG.info("Stopped LoraLuftdatenForwarder application");
    }

    private static LoraForwarderConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (FileInputStream fis = new FileInputStream(file)) {
            return mapper.readValue(fis, LoraForwarderConfig.class);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            LoraForwarderConfig config = new LoraForwarderConfig();
            mapper.writeValue(file, config);
            return config;
        }
    }

}
