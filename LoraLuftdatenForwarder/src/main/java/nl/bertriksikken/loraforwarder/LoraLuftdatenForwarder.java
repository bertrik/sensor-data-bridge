package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.pm.LoraMessage;
import nl.bertriksikken.pm.SensorMessage;
import nl.bertriksikken.pm.SensorSds;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.dto.TtnUplinkMessage;

public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.properties";
    private static final String SOFTWARE_VERSION = "0.1";

    private final MqttListener mqttListener;
    private final LuftdatenUploader uploader;
    private final ExecutorService executor;

    public static void main(String[] args) throws IOException, MqttException {
        ILoraForwarderConfig config = readConfig(new File(CONFIG_FILE));
        LoraLuftdatenForwarder app = new LoraLuftdatenForwarder(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private LoraLuftdatenForwarder(ILoraForwarderConfig config) {

        ILuftdatenApi restClient = LuftdatenUploader.newRestClient(config.getLuftdatenUrl(),
                config.getLuftdatenTimeout());
        uploader = new LuftdatenUploader(restClient, SOFTWARE_VERSION);
        executor = Executors.newSingleThreadExecutor();

        mqttListener = new MqttListener(this::messageReceived, config.getMqttUrl(), config.getMqttAppId(),
                config.getMqttAppKey());
    }

    void messageReceived(Instant instant, String topic, String message) {
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

        // decode payload
        String sensorId = String.format(Locale.ROOT, "TTN-%s", uplink.getHardwareSerial());
        byte[] payload = uplink.getRawPayload();
        LoraMessage loraMessage;
        try {
            loraMessage = LoraMessage.decode(payload);
        } catch (ParseException e) {
            LOG.warn("Could not parse payload");
            return;
        }

        LOG.info("Dust data: PM10 = {} ug/m3, PM2.5 = {} ug/m3", loraMessage.getPm10(), loraMessage.getPm2_5());

        // encode as luftdaten
        SensorSds sds = new SensorSds(sensorId, loraMessage.getPm10(), loraMessage.getPm2_5());
        SensorMessage sensorMessage = new SensorMessage(sds);

        // schedule upload
        executor.execute(() -> handleMessage(sensorId, sensorMessage));
    }

    private void handleMessage(String sensorId, SensorMessage sensorMessage) {
        // forward to luftdaten, in an exception safe manner
        try {
            uploader.uploadMeasurement(sensorId, sensorMessage);
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
