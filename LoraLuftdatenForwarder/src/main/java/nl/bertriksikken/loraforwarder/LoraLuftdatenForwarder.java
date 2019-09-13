package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.loraforwarder.rudzl.dto.RudzlMessage;
import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.luftdaten.dto.LuftdatenItem;
import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.pm.SensorBme;
import nl.bertriksikken.pm.SensorMessage;
import nl.bertriksikken.pm.SensorSds;
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
    }

    // package-private to allow testing
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
        String sensorId = String.format(Locale.ROOT, "TTN-%s", uplink.getHardwareSerial());

        SensorMessage sensorMessage = decodeTtnMessage(instant, sensorId, uplink);

        // schedule upload
        if (sensorMessage != null) {
        	executor.execute(() -> handleMessageTask(sensorId, sensorMessage));
        }
    }
    
    private SensorMessage decodeTtnMessage(Instant instant, String sensorId, TtnUplinkMessage uplinkMessage) {
    	switch (encoding) {
    	case RUDZL:
    		RudzlMessage message = new RudzlMessage(uplinkMessage.getPayloadFields());
            SensorSds sds = new SensorSds(sensorId, message.getPM10(), message.getPM2_5());
            SensorMessage sensorMessage = new SensorMessage(sds);    	
            SensorBme bme = new SensorBme(message.getT(), message.getRH(), message.getP());
            sensorMessage.setBme(bme);
    		return sensorMessage;
    	default:
    		return null;
    	}
    }

    private void handleMessageTask(String sensorId, SensorMessage sensorMessage) {
        // forward to luftdaten, in an exception safe manner
        try {
        	LuftdatenMessage sdsMessage = new LuftdatenMessage(SOFTWARE_VERSION);
        	sdsMessage.addItem(new LuftdatenItem("P1", sensorMessage.getSds().getPm10()));
        	sdsMessage.addItem(new LuftdatenItem("P2", sensorMessage.getSds().getPm2_5()));
            uploader.uploadMeasurement(sensorId, LuftdatenUploader.PIN_SDS, sdsMessage);

            if (sensorMessage.getBme().isPresent()) {
            	SensorBme bme = sensorMessage.getBme().get();
	        	LuftdatenMessage bmeMessage = new LuftdatenMessage(SOFTWARE_VERSION);
	        	bmeMessage.addItem(new LuftdatenItem("temperature", bme.getTemp()));
	        	bmeMessage.addItem(new LuftdatenItem("humidity", bme.getRh()));
	        	bmeMessage.addItem(new LuftdatenItem("pressure", bme.getPressure()));
	            uploader.uploadMeasurement(sensorId, LuftdatenUploader.PIN_BME, bmeMessage);
            }
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
