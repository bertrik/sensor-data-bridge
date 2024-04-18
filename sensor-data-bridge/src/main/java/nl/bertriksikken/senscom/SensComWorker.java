package nl.bertriksikken.senscom;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.loraforwarder.util.CatchingRunnable;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sensor.community worker, handles uploads on its own executor, one per group of devices.
 */
final class SensComWorker {
    private static final Logger LOG = LoggerFactory.getLogger(SensComWorker.class);

    private static final String USER_AGENT = "github.com/bertrik/sensor-data-bridge";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ObjectMapper mapper;
    private final ISensComApi restClient;
    private final String softwareVersion;
    private final String appId;

    // map from device id to sensor.community id
    private final Map<AppDeviceId, String> sensComIds = new HashMap<>();

    SensComWorker(ObjectMapper mapper, ISensComApi restClient, String softwareVersion, String appId) {
        this.mapper = Objects.requireNonNull(mapper);
        this.restClient = Objects.requireNonNull(restClient);
        this.softwareVersion = softwareVersion;
        this.appId = appId;
    }

    /**
     * Creates a new sensor.community REST client.
     */
    public static SensComWorker create(ObjectMapper mapper, SensComConfig config, String version, String id) {
        LOG.info("Creating new REST client for '{}' with timeout {}", config.getUrl(), config.getTimeout());
        Duration timeout = config.getTimeout();
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(SensComWorker::addUserAgent)
                .connectTimeout(timeout).readTimeout(timeout).writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        ISensComApi restClient = retrofit.create(ISensComApi.class);
        return new SensComWorker(mapper, restClient, version, id);
    }

    private static okhttp3.Response addUserAgent(Interceptor.Chain chain) throws IOException {
        Request userAgentRequest = chain.request().newBuilder().header("User-Agent", USER_AGENT).build();
        return chain.proceed(userAgentRequest);
    }

    void stop() {
        LOG.info("Stopping sensor.community worker '{}'", appId);
        executor.shutdownNow();
    }

    // schedules an upload to all pins
    void scheduleUpload(AppDeviceId appDeviceId, SensorData data) {
        executor.execute(new CatchingRunnable(LOG, () -> performUpload(appDeviceId, data)));
    }

    // uploads to all pins, runs on our executor
    private void performUpload(AppDeviceId appDeviceId, SensorData data) {
        // look up custom sensor.community id
        String sensorId = sensComIds.getOrDefault(appDeviceId, "");
        if (sensorId.isEmpty()) {
            // no sensor.community id found, so no upload
            return;
        }

        // pin 1 (dust sensors)
        if (data.hasValue(ESensorItem.PM10) || data.hasValue(ESensorItem.PM2_5) || data.hasValue(ESensorItem.PM1_0)
                || data.hasValue(ESensorItem.PM4_0)) {
            SensComMessage p1Message = new SensComMessage(softwareVersion);

            addItem(p1Message, data, ESensComItem.PM10);
            addItem(p1Message, data, ESensComItem.PM4);
            addItem(p1Message, data, ESensComItem.PM2_5);
            addItem(p1Message, data, ESensComItem.PM1_0);

            addItem(p1Message, data, ESensComItem.PM10_N);
            addItem(p1Message, data, ESensComItem.PM4_0_N);
            addItem(p1Message, data, ESensComItem.PM2_5_N);
            addItem(p1Message, data, ESensComItem.PM1_0_N);
            addItem(p1Message, data, ESensComItem.PM0_5_N);

            // encode particle size with 3 decimals
            if (data.hasValue(ESensorItem.PM_TPS)) {
                addItem(p1Message, data, ESensComItem.PM_TPS);
            }

            uploadMeasurement(ESensComPin.PARTICULATE_MATTER, sensorId, p1Message);
        }

        // pin 3: temperature & pressure, but no humidity
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.PRESSURE)
                && !data.hasValue(ESensorItem.HUMIDITY)) {
            SensComMessage p3Message = new SensComMessage(softwareVersion);
            addItem(p3Message, data, ESensComItem.TEMPERATURE);
            addItem(p3Message, data, ESensComItem.PRESSURE);
            uploadMeasurement(ESensComPin.TEMPERATURE_PRESSURE, sensorId, p3Message);
        }

        // pin 7: temperature & humidity, but no pressure
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.HUMIDITY)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p7Message = new SensComMessage(softwareVersion);
            addItem(p7Message, data, ESensComItem.TEMPERATURE);
            addItem(p7Message, data, ESensComItem.HUMIDITY);
            uploadMeasurement(ESensComPin.TEMPERATURE_HUMIDITY, sensorId, p7Message);
        }

        // pin 9: position
        if (data.hasValue(ESensorItem.POS_LAT) && data.hasValue(ESensorItem.POS_LON)) {
            SensComMessage p9Message = new SensComMessage(softwareVersion);
            addItem(p9Message, data, ESensComItem.GPS_LAT);
            addItem(p9Message, data, ESensComItem.GPS_LON);
            if (data.hasValue(ESensorItem.POS_ALT)) {
                addItem(p9Message, data, ESensComItem.GPS_ALT);
            }
            uploadMeasurement(ESensComPin.POSITION, sensorId, p9Message);
        }

        // pin 11: temperature & humidity & pressure
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.HUMIDITY)
                && data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p11Message = new SensComMessage(softwareVersion);
            addItem(p11Message, data, ESensComItem.TEMPERATURE);
            addItem(p11Message, data, ESensComItem.HUMIDITY);
            addItem(p11Message, data, ESensComItem.PRESSURE);
            uploadMeasurement(ESensComPin.TEMPERATURE_HUMIDITY_PRESSURE, sensorId, p11Message);
        }

        // pin 13: only temperature
        if (data.hasValue(ESensorItem.TEMPERATURE) && !data.hasValue(ESensorItem.HUMIDITY)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p13Message = new SensComMessage(softwareVersion);
            addItem(p13Message, data, ESensComItem.TEMPERATURE);
            uploadMeasurement(ESensComPin.TEMPERATURE, sensorId, p13Message);
        }

        // pin 15: noise
        if (data.hasValue(ESensorItem.NOISE_LA_MIN) && data.hasValue(ESensorItem.NOISE_LA_EQ)
                && data.hasValue(ESensorItem.NOISE_LA_MAX)) {
            SensComMessage p15Message = new SensComMessage(softwareVersion);
            addItem(p15Message, data, ESensComItem.NOISE_LA_EQ);
            addItem(p15Message, data, ESensComItem.NOISE_LA_MIN);
            addItem(p15Message, data, ESensComItem.NOISE_LA_MAX);
            uploadMeasurement(ESensComPin.NOISE, sensorId, p15Message);
        }
    }

    private void uploadMeasurement(ESensComPin pin, String sensorId, SensComMessage message) {
        try {
            LOG.info("Uploading for {} ({}) to pin {}: '{}'", appId, sensorId, pin, mapper.writeValueAsString(message));
            Instant startTime = Instant.now();
            Response<String> response = restClient.pushSensorData(pin.getPin(), sensorId, message).execute();
            long millis = Duration.between(startTime, Instant.now()).toMillis();
            if (response.isSuccessful()) {
                LOG.info("Upload success for {} in {} ms: {}", sensorId, millis, response.body());
            } else {
                LOG.warn("Upload failed for {}: {} - {}", sensorId, response.message(), response.errorBody().string());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException for {}: {}", sensorId, e.getMessage());
        }
    }

    private void addItem(SensComMessage message, SensorData data, ESensComItem sensComItem) {
        ESensorItem item = sensComItem.getItem();
        if (data.hasValue(item)) {
            String value = sensComItem.format(data.getValue(item));
            message.addItem(sensComItem.getId(), value);
        }
    }

    void scheduleProcessAttributes(Map<String, AttributeMap> deviceAttributes) {
        executor.execute(new CatchingRunnable(LOG, () -> processAttributes(deviceAttributes)));
    }

    private void processAttributes(Map<String, AttributeMap> deviceAttributes) {
        sensComIds.clear();
        deviceAttributes.forEach(this::processDeviceAttributes);
        sensComIds.forEach((key, value) -> LOG.info("SensCom mapping: {} -> {}", key, value));
    }

    private void processDeviceAttributes(String devId, AttributeMap attributes) {
        String sensComId = attributes.getOrDefault("senscom-id", "").trim();
        if (!sensComId.isEmpty()) {
            AppDeviceId appDeviceId = new AppDeviceId(appId, devId);
            sensComIds.put(appDeviceId, sensComId);
        }
    }

}
