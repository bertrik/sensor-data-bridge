package nl.bertriksikken.mydevices;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.mydevices.dto.MyDevicesMessage;
import nl.sikken.bertrik.cayenne.CayenneMessage;

public final class MyDevicesMqttUploader {

    private static final Logger LOG = LoggerFactory.getLogger(MyDevicesMqttUploader.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MqttClient mqttClient;
    private final MqttConnectOptions options;
    private final String topic;

    MyDevicesMqttUploader(MyDevicesConfig config) {
        try {
            this.mqttClient = new MqttClient(config.getMqttUrl(), config.getClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new IllegalArgumentException(e);
        }

        // create connect options
        options = new MqttConnectOptions();
        options.setUserName(config.getUser());
        options.setPassword(config.getPass().toCharArray());
        options.setAutomaticReconnect(true);
        topic = String.join ("/", "v1", config.getUser(), "things", config.getClientId(), "data", "json");
    }

    public void start() throws MqttException {
        mqttClient.connect(options);
    }

    public void stop() throws MqttException {
        mqttClient.disconnect();
    }

    public void upload(CayenneMessage cayenneMessage) {
        MyDevicesMessage message = MyDevicesMessage.fromCayenne(cayenneMessage);
        try {
            byte[] payload = objectMapper.writeValueAsBytes(message);
            LOG.info("Payload: {}", objectMapper.writeValueAsString(message));
            mqttClient.publish(topic, payload, 0, true);
        } catch (JsonProcessingException e) {
            LOG.error("JsonProcessingException: {}", e.getMessage());
        } catch (MqttException e) {
            LOG.error("MqttException: {}", e.getMessage());
        }
    }

}
