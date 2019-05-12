package nl.bertriksikken.ttn;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * A "do-nothing" adapter for the MQTT callback, so you can override individual methods.
 */
public class MqttCallbackAdapter implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        // do nothing
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // do nothing
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // do nothing
    }

}
