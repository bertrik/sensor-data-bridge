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

import nl.bertriksikken.ttnv2.dto.TtnUplinkMessage;
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
     * @param callback the interface for indicating a received message.
     * @param url      the URL of the MQTT server
     * @param appId    the name of the TTN application
     * @param appKey   the key of the TTN application
     */
    public MqttListener(TtnConfig config, TtnAppConfig appConfig, IMessageReceived callback) {
        String url = (appConfig.getVersion() == ETtnStackVersion.V2) ? config.getUrlV2() : config.getUrlV3();
        String topic = (appConfig.getVersion() == ETtnStackVersion.V2) ? "+/devices/+/up" : "v3/+/devices/+/up";
        
        LOG.info("Creating client for MQTT server '{}' for app '{}'", url, appConfig.getName());
        try {
            this.mqttClient = new MqttClient(url, MqttClient.generateClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new IllegalArgumentException(e);
        }
        
        mqttClient.setCallback(new MqttCallbackHandler(mqttClient, topic, callback, appConfig.getVersion()));

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
        private final ETtnStackVersion version;

        private MqttCallbackHandler(MqttClient client, String topic, IMessageReceived listener,
                ETtnStackVersion version) {
            this.client = client;
            this.topic = topic;
            this.listener = listener;
            this.version = version;
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
                String deviceEui;
                byte[] payload;
                switch (version) {
                case V2:
                    TtnUplinkMessage uplinkV2 = mapper.readValue(message, TtnUplinkMessage.class);
                    deviceEui = uplinkV2.getHardwareSerial();
                    payload = uplinkV2.getRawPayload();
                    break;
                case V3:
                    Ttnv3UplinkMessage uplinkV3 = mapper.readValue(message, Ttnv3UplinkMessage.class);
                    deviceEui = uplinkV3.getEndDeviceIds().getDeviceEui();
                    payload = uplinkV3.getUplinkMessage().getPayload();
                    break;
                default:
                    throw new IllegalStateException("Unhandled TTN version " + version);
                }
                
                // notify listener
                listener.messageReceived(deviceEui, payload);
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
