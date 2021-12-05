package nl.bertriksikken.ttn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttnv3.dto.Ttnv3UplinkMessage;

/**
 * Listener process for receiving data from MQTT.
 * 
 * Decouples the MQTT callback from listener using a single thread executor.
 */
public final class MqttListener {

    private static final Logger LOG = LoggerFactory.getLogger(MqttListener.class);
    private static final long DISCONNECT_TIMEOUT_MS = 3000;

    private final MqttClient mqttClient;
    private final MqttConnectOptions options;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor.
     * 
     * @param config the global TTN configuration
     * @param appConfig the application-specific configuration
     * @param callback callback for message notification
     */
    public MqttListener(TtnConfig config, TtnAppConfig appConfig, IMessageReceived callback) {
        LOG.info("Creating MQTT client for app '{}'", appConfig.getName());
        try {
            this.mqttClient = new MqttClient(config.getMqttUrl(), MqttClient.generateClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new IllegalArgumentException(e);
        }
        
        mqttClient.setCallback(new MqttCallbackHandler(mqttClient, "v3/+/devices/+/up", callback));

        // create connect options
        options = new MqttConnectOptions();
        options.setUserName(appConfig.getName());
        options.setPassword(appConfig.getKey().toCharArray());
        options.setAutomaticReconnect(true);
    }

    /**
     * Starts this module.
     * 
     * @throws MqttException in case something went wrong with MQTT
     */
    public void start() throws MqttException {
        LOG.info("Starting MQTT listener '{}'", options.getUserName());

        mqttClient.connect(options);
    }

    public void stop() {
        LOG.info("Stopping MQTT listener '{}'", options.getUserName());
        try {
            mqttClient.disconnect(DISCONNECT_TIMEOUT_MS);
        } catch (MqttException e) {
            // don't care, just log
            LOG.warn("Caught exception on disconnect: {}", e.getMessage());
        }
    }

    /**
     * MQTT callback handler, (re-)subscribes to the topic and forwards incoming
     * messages.
     */
    private final class MqttCallbackHandler implements MqttCallbackExtended {

        private final MqttClient client;
        private final String topic;
        private final IMessageReceived listener;

        private MqttCallbackHandler(MqttClient client, String topic, IMessageReceived listener) {
            this.client = client;
            this.topic = topic;
            this.listener = listener;
        }

        @Override
        public void connectionLost(Throwable cause) {
            LOG.warn("Connection lost: {}", cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            LOG.info("Message arrived on topic '{}'", topic);

            // handle message, in an exception safe manner
            try {
                // decode JSON
                String message = new String(mqttMessage.getPayload(), StandardCharsets.US_ASCII);

                // parse device EUI and payload
                Ttnv3UplinkMessage uplinkV3 = mapper.readValue(message, Ttnv3UplinkMessage.class);
                TtnUplinkMessage uplink = uplinkV3.toTtnUplinkMessage();
                
                // notify listener
                if (uplink.getRawPayload().length > 0) {
                    listener.messageReceived(uplink);
                } else {
                    LOG.info("Ignoring empty payload");
                }
            } catch (IOException e) {
                LOG.warn("Could not parse MQTT message: '{}'", mqttMessage);
            } catch (Exception e) {
                LOG.trace("Caught unhandled uplink exception in MQTT listener", e);
                LOG.error("Caught unhandled uplink exception in MQTT listener: {}", e.getMessage());
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // nothing to do
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LOG.info("Connected to '{}', subscribing to MQTT topic '{}'", serverURI, topic);
            try {
                client.subscribe(topic);
            } catch (MqttException e) {
                LOG.error("Caught exception while subscribing!");
            }
        }
    }

}
