package nl.bertriksikken.mydevices;

import nl.bertriksikken.mydevices.dto.MyDevicesMessage;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;

public final class MyDevicesHttpUploaderTest {

    public static void main(String[] args) {
        MyDevicesHttpUploaderTest test = new MyDevicesHttpUploaderTest();
        test.testUpload();
    }

    public void testUpload() {
        SensorData data = new SensorData();
        data.addValue(ESensorItem.PM10, 10.0);
        data.addValue(ESensorItem.PM4_0, 4.0);
        data.addValue(ESensorItem.PM2_5, 2.5);
        data.addValue(ESensorItem.PM1_0, 1.0);
        data.addValue(ESensorItem.HUMI, 50.0);
        data.addValue(ESensorItem.TEMP, 21.3);
        data.addValue(ESensorItem.PRESSURE, 102345.0);
        MyDevicesMessage message = MyDevicesMessage.fromSensorData(data);

        MyDevicesConfig config = new MyDevicesConfig();
        MyDevicesHttpUploader uploader = MyDevicesHttpUploader.create(config);

        MyDevicesCredentials credentials = new MyDevicesCredentials("1b3d0d60-9b7e-11e7-a1da-536ee79fd847",
                "f865f85afcb51f0a3c732d0c2910d93d8cd3b59e", "e78a12a0-98aa-11eb-a2e4-b32ea624e442");
        uploader.uploadMeasurement("device", credentials, message);
    }

}
