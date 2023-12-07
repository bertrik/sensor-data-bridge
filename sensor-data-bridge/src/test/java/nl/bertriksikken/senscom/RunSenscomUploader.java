package nl.bertriksikken.senscom;

import java.util.HashMap;
import java.util.Map;

import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;

/**
 * Runs the SensComUploader to send a basic message to a local server, in order
 * to inspect the actual HTTP request.
 * <p>
 * Use with (for example):
 * https://gist.github.com/mdonkers/63e115cc0c79b4f6b8b3a6b797e485c7
 */
public final class RunSenscomUploader {

    public static void main(String[] args) {
        RunSenscomUploader runner = new RunSenscomUploader();
        runner.run();
    }

    private void run() {
        SensComConfig config = new SensComConfig("http://localhost:8080", 10);
        SensComUploader uploader = SensComUploader.create(config, "test");
        uploader.start();

        AppDeviceId id = new AppDeviceId("app", "device");
        Map<AppDeviceId, AttributeMap> attributes = new HashMap<>();
        attributes.put(id, new AttributeMap(Map.of("senscom-id", "sensor")));
        uploader.scheduleProcessAttributes(attributes);

        SensorData sensorData = new SensorData();
        sensorData.addValue(ESensorItem.TEMPERATURE, 12.34);
        uploader.scheduleUpload(id, sensorData);
    }

}
