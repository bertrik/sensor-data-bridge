package nl.bertriksikken.ttn;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener process for receiving data from MQTT.
 * 
 * Decouples the MQTT callback from listener using a single thread executor. 
 */
public final class MqttListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(MqttListener.class);
    private static final long DISCONNECT_TIMEOUT_MS = 3000;
    
    private final IMessageReceived callback;
    private final String appId;
    private final String appKey;

    private MqttClient mqttClient;

    /**
     * Constructor.
     * 
     * @param callback the interface for indicating a received message.
     * @param url the URL of the MQTT server
     * @param topic the topic to listen to
     */
    public MqttListener(IMessageReceived callback, String url, String appId, String appKey) {
        this.callback = callback;
        this.appId = appId;
        this.appKey = appKey;
        
        LOG.info("Creating client for MQTT server {}", url);
        try {
			this.mqttClient = new MqttClient(url, MqttClient.generateClientId(), new MemoryPersistence());
		} catch (MqttException e) {
			throw new IllegalArgumentException(e);
		}
    }
    
    /**
     * Starts this module.
     * 
     * @throws MqttException in case something went wrong with MQTT 
     */
    public void start() throws MqttException {
        LOG.info("Starting MQTT listener");
        
        // connect
        LOG.info("Connecting to MQTT server as user {}", appId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(appId);
        options.setPassword(appKey.toCharArray());
        // automatic reconnect does not work in practice, I prefer a hard exception over a silent hang
        options.setAutomaticReconnect(false);
        options.setKeepAliveInterval(10);
        mqttClient.connect(options);
        
        // subscribe
        String topic = "+/devices/+/up";
        LOG.info("Subscribing to topic '{}'", topic);
        mqttClient.subscribe(topic, this::messageArrived);
    }
    
    /**
     * Handles an incoming message.
     * 
     * @param topic the topic
     * @param mqttMessage the message
     * @throws Exception who knows?
     */
    private void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
    	Instant now = Instant.now();
        final String message = new String(mqttMessage.getPayload(), StandardCharsets.US_ASCII);
        LOG.info("Message arrived on topic '{}'", topic);
        
        // notify our listener, in an exception safe manner
        try {
            callback.messageReceived(now, topic, message);
        } catch (Exception e) {
            LOG.trace("Caught exception", e);
            LOG.error("Caught exception in MQTT listener: {}", e.getMessage());
        }
    }
    
    /**
     * Stops this module.
     */
    public void stop() {
        LOG.info("Stopping MQTT listener");
        try {
            mqttClient.disconnect(DISCONNECT_TIMEOUT_MS);
        } catch (MqttException e) {
            // don't care, just log
            LOG.warn("Caught exception on disconnect: {}", e.getMessage());
        } finally {
            try {
                mqttClient.close();
            } catch (MqttException e) {
                // don't care, just log
                LOG.warn("Caught exception on close: {}", e.getMessage());
            }
        }
    }
    
}
