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
    
    private final String clientId;
    private final IMessageReceived callback;
    private final String url;
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
        this.clientId = MqttClient.generateClientId();
        this.callback = callback;
        this.url = url;
        this.appId = appId;
        this.appKey = appKey;
        
        try {
			this.mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
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
        LOG.info("Connecting to MQTT server {}", url);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(appId);
        options.setPassword(appKey.toCharArray());
        options.setAutomaticReconnect(true);
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
        LOG.info("Message arrived on topic '{}': {}", topic, message);
        // notify our listener
        callback.messageReceived(now, topic, message);
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
