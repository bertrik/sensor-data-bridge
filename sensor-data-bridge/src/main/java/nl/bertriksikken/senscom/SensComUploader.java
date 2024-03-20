package nl.bertriksikken.senscom;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.loraforwarder.IUploader;
import nl.bertriksikken.pm.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uploader for sensor.community, delegates work to a worker, one for each application.
 */
public final class SensComUploader implements IUploader {

    private static final Logger LOG = LoggerFactory.getLogger(SensComUploader.class);

    private final Map<String, SensComWorker> workerMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SensComConfig config;
    private final String softwareVersion;

    SensComUploader(SensComConfig config, String softwareVersion) {
        this.config = Objects.requireNonNull(config);
        this.softwareVersion = softwareVersion;
    }

    public static SensComUploader create(SensComConfig config, String softwareVersion) {
        return new SensComUploader(config, softwareVersion);
    }

    @Override
    public void start() {
        LOG.info("Starting sensor.community uploader");
    }

    @Override
    public void stop() {
        LOG.info("Stopping sensor.community uploader");
        List.copyOf(workerMap.values()).forEach(SensComWorker::stop);
    }

    private SensComWorker getOrCreateWorker(String id) {
        return workerMap.computeIfAbsent(id, name -> SensComWorker.create(mapper, config, softwareVersion, id));
    }

    @Override
    public void scheduleUpload(AppDeviceId appDeviceId, SensorData data) {
        SensComWorker worker = getOrCreateWorker(appDeviceId.getAppName());
        worker.scheduleUpload(appDeviceId, data);
    }

    @Override
    public void scheduleProcessAttributes(String appId, Map<String, AttributeMap> deviceAttributes) {
        SensComWorker worker = getOrCreateWorker(appId);
        worker.scheduleProcessAttributes(deviceAttributes);
    }
}
