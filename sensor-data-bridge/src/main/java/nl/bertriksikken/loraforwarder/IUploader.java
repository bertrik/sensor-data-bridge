package nl.bertriksikken.loraforwarder;

import java.util.Map;

import nl.bertriksikken.pm.SensorData;

public interface IUploader {

    void start();

    void stop();

    void scheduleProcessAttributes(Map<AppDeviceId, AttributeMap> attributes);

    void scheduleUpload(AppDeviceId appDeviceId, SensorData data);

}
