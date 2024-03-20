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
import java.util.Locale;
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

            addSimpleItem(data, p1Message, ESensorItem.PM10, "P1");
            addSimpleItem(data, p1Message, ESensorItem.PM4_0, "P4");
            addSimpleItem(data, p1Message, ESensorItem.PM2_5, "P2");
            addSimpleItem(data, p1Message, ESensorItem.PM1_0, "P0");

            addSimpleItem(data, p1Message, ESensorItem.PM10_N, "N10");
            addSimpleItem(data, p1Message, ESensorItem.PM4_0_N, "N4");
            addSimpleItem(data, p1Message, ESensorItem.PM2_5_N, "N25");
            addSimpleItem(data, p1Message, ESensorItem.PM1_0_N, "N1");
            addSimpleItem(data, p1Message, ESensorItem.PM0_5_N, "N05");

            // encode particle size with 3 decimals
            if (data.hasValue(ESensorItem.PM_TPS)) {
                p1Message.addItem("TS", String.format(Locale.ROOT, "%.3f", data.getValue(ESensorItem.PM_TPS)));
            }

            uploadMeasurement(ESensComPin.PARTICULATE_MATTER, sensorId, p1Message);
        }

        // pin 3: temperature & pressure, but no humidity
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.PRESSURE)
                && !data.hasValue(ESensorItem.HUMIDITY)) {
            SensComMessage p3Message = new SensComMessage(softwareVersion);
            p3Message.addItem("temperature", data.getValue(ESensorItem.TEMPERATURE));
            p3Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            uploadMeasurement(ESensComPin.TEMPERATURE_PRESSURE, sensorId, p3Message);
        }

        // pin 7: temperature & humidity, but no pressure
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.HUMIDITY)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p7Message = new SensComMessage(softwareVersion);
            p7Message.addItem("temperature", data.getValue(ESensorItem.TEMPERATURE));
            p7Message.addItem("humidity", data.getValue(ESensorItem.HUMIDITY));
            uploadMeasurement(ESensComPin.TEMPERATURE_HUMIDITY, sensorId, p7Message);
        }

        // pin 9: position
        if (data.hasValue(ESensorItem.POS_LAT) && data.hasValue(ESensorItem.POS_LON)) {
            SensComMessage p9Message = new SensComMessage(softwareVersion);
            p9Message.addItem("latitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LAT)));
            p9Message.addItem("longitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LON)));
            if (data.hasValue(ESensorItem.POS_ALT)) {
                p9Message.addItem("altitude", data.getValue(ESensorItem.POS_ALT));
            }
            uploadMeasurement(ESensComPin.POSITION, sensorId, p9Message);
        }

        // pin 11: temperature & humidity & pressure
        if (data.hasValue(ESensorItem.TEMPERATURE) && data.hasValue(ESensorItem.HUMIDITY)
                && data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p11Message = new SensComMessage(softwareVersion);
            p11Message.addItem("temperature", data.getValue(ESensorItem.TEMPERATURE));
            p11Message.addItem("humidity", data.getValue(ESensorItem.HUMIDITY));
            p11Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            uploadMeasurement(ESensComPin.TEMPERATURE_HUMIDITY_PRESSURE, sensorId, p11Message);
        }

        // pin 13: only temperature
        if (data.hasValue(ESensorItem.TEMPERATURE) && !data.hasValue(ESensorItem.HUMIDITY)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p13Message = new SensComMessage(softwareVersion);
            p13Message.addItem("temperature", data.getValue(ESensorItem.TEMPERATURE));
            uploadMeasurement(ESensComPin.TEMPERATURE, sensorId, p13Message);
        }

        // pin 15: noise
        if (data.hasValue(ESensorItem.NOISE_LA_MIN) && data.hasValue(ESensorItem.NOISE_LA_EQ)
                && data.hasValue(ESensorItem.NOISE_LA_MAX)) {
            SensComMessage p15Message = new SensComMessage(softwareVersion);
            p15Message.addItem("noise_LAeq",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_EQ)));
            p15Message.addItem("noise_LA_min",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_MIN)));
            p15Message.addItem("noise_LA_max",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_MAX)));
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

    private void addSimpleItem(SensorData data, SensComMessage message, ESensorItem item, String name) {
        if (data.hasValue(item)) {
            double value = data.getValue(item);
            message.addItem(name, value);
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
