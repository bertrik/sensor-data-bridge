package nl.bertriksikken.opensense;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.loraforwarder.IUploader;
import nl.bertriksikken.loraforwarder.util.CatchingRunnable;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.senscom.SensComMessage;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class OpenSenseUploader implements IUploader {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSenseUploader.class);
    private static final String SOFTWARE_VERSION = "https://github.com/bertrik/sensor-data-bridge";

    private final IOpenSenseRestApi restClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<AppDeviceId, String> boxIds = new ConcurrentHashMap<>();

    OpenSenseUploader(IOpenSenseRestApi restClient) {
        this.restClient = Objects.requireNonNull(restClient);
    }

    public static OpenSenseUploader create(OpenSenseConfig config) {
        LOG.info("Creating new REST client for '{}' with timeout {}", config.getUrl(), config.getTimeout());

        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout)
                .writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        IOpenSenseRestApi restClient = retrofit.create(IOpenSenseRestApi.class);
        return new OpenSenseUploader(restClient);
    }

    @Override
    public void start() {
        LOG.info("Starting OpenSense uploader");
    }

    @Override
    public void stop() {
        LOG.info("Stopping OpenSense uploader");
        executor.shutdown();
    }

    @Override
    public void scheduleUpload(AppDeviceId appDeviceId, SensorData data) {
        String boxId = boxIds.getOrDefault(appDeviceId, "");
        if (boxId.isEmpty()) {
            return;
        }

        SensComMessage message = new SensComMessage(SOFTWARE_VERSION);

        // particulate matter
        String pmPrefix = getPmPrefix(data);
        if (data.hasValue(ESensorItem.PM10)) {
            message.addItem(pmPrefix + "P1", data.getValue(ESensorItem.PM10));
        }
        if (data.hasValue(ESensorItem.PM2_5)) {
            message.addItem(pmPrefix + "P2", data.getValue(ESensorItem.PM2_5));
        }
        if (data.hasValue(ESensorItem.PM1_0)) {
            message.addItem(pmPrefix + "P0", data.getValue(ESensorItem.PM2_5));
        }
        if (data.hasValue(ESensorItem.PM4_0)) {
            message.addItem(pmPrefix + "P4", data.getValue(ESensorItem.PM4_0));
        }

        // humidity/temperature/pressure
        String meteoPrefix = getMeteoPrefix(data);
        if (data.hasValue(ESensorItem.HUMIDITY)) {
            message.addItem(meteoPrefix + "humidity", data.getValue(ESensorItem.HUMIDITY));
        }
        if (data.hasValue(ESensorItem.TEMPERATURE)) {
            message.addItem(meteoPrefix + "temperature", data.getValue(ESensorItem.TEMPERATURE));
        }
        if (data.hasValue(ESensorItem.PRESSURE)) {
            message.addItem(meteoPrefix + "pressure", data.getValue(ESensorItem.PRESSURE));
        }

        // schedule upload
        String sensComId = "TTN-" + appDeviceId.getDeviceId();
        executor.execute(new CatchingRunnable(LOG, () -> uploadMeasurement(boxId, sensComId, message)));
    }

    private String getMeteoPrefix(SensorData data) {
        if (data.hasValue(ESensorItem.HUMIDITY) && data.hasValue(ESensorItem.TEMPERATURE)
                && data.hasValue(ESensorItem.PRESSURE)) {
            return "BME280_";
        }
        return "";
    }

    private String getPmPrefix(SensorData data) {
        if (data.hasValue(ESensorItem.PM10) && data.hasValue(ESensorItem.PM2_5) && data.hasValue(ESensorItem.PM1_0)
                && data.hasValue(ESensorItem.PM4_0)) {
            return "SPS30_";
        }
        if (data.hasValue(ESensorItem.PM10) && data.hasValue(ESensorItem.PM2_5) && data.hasValue(ESensorItem.PM1_0)) {
            return "PMS_";
        }
        return "SDS_";
    }

    private void uploadMeasurement(String boxId, String sensComId, SensComMessage message) {
        LOG.info("Upload for {} to opensense box {}: {}", sensComId, boxId, message);
        try {
            Response<String> response = restClient.postNewMeasurements(boxId, true, message).execute();
            if (response.isSuccessful()) {
                String result = response.body();
                LOG.info("Upload for {} to opensense box {} success: {}", sensComId, boxId, result);
            } else {
                LOG.warn("Upload for {} to opensense box {} failure: {} - {}", sensComId, boxId, response.code(),
                        response.errorBody().string());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

    @Override
    public void scheduleProcessAttributes(Map<AppDeviceId, AttributeMap> attributes) {
        executor.execute(new CatchingRunnable(LOG, () -> processAttributes(attributes)));
    }

    private void processAttributes(Map<AppDeviceId, AttributeMap> attributes) {
        boxIds.clear();
        attributes.forEach((appDevId, attr) -> processDeviceAttributes(boxIds, appDevId, attr));
        boxIds.forEach((device, id) -> LOG.info("Opensense mapping: {} -> {}", device, id));
    }

    private void processDeviceAttributes(Map<AppDeviceId, String> map, AppDeviceId device, AttributeMap attributes) {
        String opensenseId = attributes.getOrDefault("opensense-id", "").trim();
        if (!opensenseId.isEmpty()) {
            map.put(device, opensenseId);
        }
    }

}
