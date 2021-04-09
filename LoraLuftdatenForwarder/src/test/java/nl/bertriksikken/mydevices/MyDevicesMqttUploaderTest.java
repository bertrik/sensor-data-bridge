package nl.bertriksikken.mydevices;

import org.eclipse.paho.client.mqttv3.MqttException;

import nl.sikken.bertrik.cayenne.CayenneException;
import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;
import nl.sikken.bertrik.cayenne.ECayenneItem;

public final class MyDevicesMqttUploaderTest {

    public static void main(String[] args) throws CayenneException, MqttException {
        MyDevicesMqttUploaderTest test = new MyDevicesMqttUploaderTest();
        test.testUpload();
    }
    
    public void testUpload() throws CayenneException, MqttException {
        MyDevicesConfig config = new MyDevicesConfig();
        MyDevicesMqttUploader uploader = new MyDevicesMqttUploader(config);

        CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(0, ECayenneItem.ANALOG_INPUT, 0.0));
        message.add(new CayenneItem(1, ECayenneItem.ANALOG_INPUT, 1.0));
        message.add(new CayenneItem(2, ECayenneItem.ANALOG_INPUT, 2.0));
        message.add(new CayenneItem(4, ECayenneItem.ANALOG_INPUT, 4.0));
        message.add(new CayenneItem(5, ECayenneItem.HUMIDITY, 50.0));
        message.add(new CayenneItem(6, ECayenneItem.TEMPERATURE, 21.3));
        message.add(new CayenneItem(7, ECayenneItem.BAROMETER, 1023.4));

        try {
            uploader.start();
            uploader.upload(message);
        } finally {
            uploader.stop();
        }
    }

}
