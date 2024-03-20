package nl.bertriksikken.loraforwarder;

import java.util.Map;

import nl.bertriksikken.pm.SensorData;

public interface IUploader {

    void start();

    void stop();

    void scheduleProcessAttributes(String applicationId, Map<String, AttributeMap> deviceAttributes);

    void scheduleUpload(AppDeviceId appDeviceId, SensorData data);

}
