package nl.bertriksikken.mydevices;

import java.time.Duration;

import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;
import nl.sikken.bertrik.cayenne.ECayenneItem;

public final class MyDevicesHttpUploaderTest {

    public static void main(String[] args) {
        MyDevicesHttpUploaderTest test = new MyDevicesHttpUploaderTest();
        test.testUpload();
    }
    
    public void testUpload() {
        CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(1, ECayenneItem.ANALOG_INPUT, 1.0));
        message.add(new CayenneItem(0, ECayenneItem.ANALOG_INPUT, 0.0));
        message.add(new CayenneItem(2, ECayenneItem.ANALOG_INPUT, 2.0));
        message.add(new CayenneItem(4, ECayenneItem.ANALOG_INPUT, 4.0));
        message.add(new CayenneItem(5, ECayenneItem.HUMIDITY, 50.0));
        message.add(new CayenneItem(6, ECayenneItem.TEMPERATURE, 21.3));
        message.add(new CayenneItem(7, ECayenneItem.BAROMETER, 1023.4));

        String url = "https://api.mydevices.com";
        IMyDevicesRestApi restApi = MyDevicesHttpUploader.newRestClient(url, Duration.ofSeconds(5));
        MyDevicesHttpUploader uploader = new MyDevicesHttpUploader(restApi);
        
        MyDevicesConfig config = new MyDevicesConfig();
        uploader.upload(message, config.getUser(), config.getPass(), config.getClientId());
    }

}
