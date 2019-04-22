package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.ttn.MqttListener;

/**
 * Main class for the forwarder application.
 */
public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.properties";
	private static final String SOFTWARE_VERSION = "0.1";

	private final ObjectMapper mapper = new ObjectMapper();
    private final MqttListener mqttListener;
	private final ILoraForwarderConfig config;
	private final LuftdatenUploader uploader;
	
	/**
	 * Constructor.
	 * 
	 * @param config
	 */
	public LoraLuftdatenForwarder(ILoraForwarderConfig config) {
		this.config = config;
		
		ILuftdatenApi restClient = LuftdatenUploader.newRestClient(config.getLuftdatenUrl(), config.getLuftdatenTimeoutMs());
		uploader = new LuftdatenUploader(restClient, SOFTWARE_VERSION);
		
		mqttListener = new MqttListener(this::messageReceived, config.getMqttUrl(), config.getMqttTopic());
	}

    private void messageReceived(Instant instant, String topic, String message) {
    	// not implemented yet
    }
	
	public static void main(String[] argc) throws IOException, MqttException {
        final ILoraForwarderConfig config = readConfig(new File(CONFIG_FILE));
        final LoraLuftdatenForwarder app = new LoraLuftdatenForwarder(config);

        Thread.setDefaultUncaughtExceptionHandler(app::handleUncaughtException);

        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
	}
	
    /**
     * Handles uncaught exceptions: log it and stop the application.
     * 
     * @param t the thread
     * @param e the exception
     */
    private void handleUncaughtException(Thread t, Throwable e) {
        LOG.error("Caught unhandled exception, application will be stopped ...", e);
        stop();
    }
    
    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting DustSensor bridge application");

        // start sub-modules
        uploader.start();
        mqttListener.start();

        LOG.info("Started DustSensor bridge application");
    }

    /**
	 * Stops the application.
	 * 
	 * @throws MqttException
	 */
	private void stop() {
	    LOG.info("Stopping DustSensor bridge application");

	    mqttListener.stop();
	    uploader.stop();

	    LOG.info("Stopped DustSensor bridge application");
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
