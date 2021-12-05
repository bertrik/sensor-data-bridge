package nl.bertriksikken.senscom;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.loraforwarder.util.CatchingRunnable;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Uploader for sensor.community
 */
public final class SensComUploader {

    private static final Logger LOG = LoggerFactory.getLogger(SensComUploader.class);
    private static final String SOFTWARE_VERSION = "https://github.com/bertrik/sensor-data-bridge";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ISensComApi restClient;

    // map from device id to sensor.community id
    private final Map<AppDeviceId, String> sensComIds = new HashMap<>();

    /**
     * Constructor.
     * 
     * @param restClient the REST client
     */
    SensComUploader(ISensComApi restClient) {
        this.restClient = restClient;
    }

    /**
     * Creates a new sensor.community REST client.
     */
    public static SensComUploader create(SensComConfig config) {
        LOG.info("Creating new REST client for '{}' with timeout {}", config.getUrl(), config.getTimeout());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeout()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        ISensComApi restClient = retrofit.create(ISensComApi.class);
        return new SensComUploader(restClient);
    }

    public void start() {
        LOG.info("Starting sensor.community uploader");
    }

    public void stop() {
        LOG.info("Stopping sensor.community uploader");
        executor.shutdown();
    }

    // schedules an upload to all pins
    public void scheduleUpload(AppDeviceId appDeviceId, SensorData data) {
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
            SensComMessage p1Message = new SensComMessage(SOFTWARE_VERSION);

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

            uploadMeasurement(sensorId, "1", p1Message);
        }

        // pin 3: temperature & pressure, but no humidity
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.PRESSURE)
                && !data.hasValue(ESensorItem.HUMI)) {
            SensComMessage p3Message = new SensComMessage(SOFTWARE_VERSION);
            p3Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p3Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            uploadMeasurement(sensorId, "3", p3Message);
        }

        // pin 7: temperature & humidity, but no pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p7Message = new SensComMessage(SOFTWARE_VERSION);
            p7Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p7Message.addItem("humidity", data.getValue(ESensorItem.HUMI));
            uploadMeasurement(sensorId, "7", p7Message);
        }

        // pin 9: position
        if (data.hasValue(ESensorItem.POS_LAT) && data.hasValue(ESensorItem.POS_LON)) {
            SensComMessage p9Message = new SensComMessage(SOFTWARE_VERSION);
            p9Message.addItem("latitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LAT)));
            p9Message.addItem("longitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LON)));
            if (data.hasValue(ESensorItem.POS_ALT)) {
                p9Message.addItem("altitude", data.getValue(ESensorItem.POS_ALT));
            }
            uploadMeasurement(sensorId, "9", p9Message);
        }

        // pin 11: temperature & humidity & pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI) && data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p11Message = new SensComMessage(SOFTWARE_VERSION);
            p11Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p11Message.addItem("humidity", data.getValue(ESensorItem.HUMI));
            p11Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            uploadMeasurement(sensorId, "11", p11Message);
        }

        // pin 13: only temperature
        if (data.hasValue(ESensorItem.TEMP) && !data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            SensComMessage p13Message = new SensComMessage(SOFTWARE_VERSION);
            p13Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            uploadMeasurement(sensorId, "13", p13Message);
        }

        // pin 15: sound
        if (data.hasValue(ESensorItem.NOISE_LA_MIN) && data.hasValue(ESensorItem.NOISE_LA_EQ)
                && data.hasValue(ESensorItem.NOISE_LA_MAX)) {
            SensComMessage p15Message = new SensComMessage(SOFTWARE_VERSION);
            p15Message.addItem("noise_LAeq",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_EQ)));
            p15Message.addItem("noise_LA_min",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_MIN)));
            p15Message.addItem("noise_LA_max",
                    String.format(Locale.ROOT, "%.1f", data.getValue(ESensorItem.NOISE_LA_MAX)));
            uploadMeasurement(sensorId, "15", p15Message);
        }

        // pin 17: NO2 not implemented
        // pin 19: radiation not implemented
    }

    private void uploadMeasurement(String sensorId, String pin, SensComMessage message) {
        try {
            LOG.info("Sending for {} to pin {}: '{}'", sensorId, pin, mapper.writeValueAsString(message));
            Response<String> response = restClient.pushSensorData(pin, sensorId, message).execute();
            if (response.isSuccessful()) {
                LOG.info("Result success: {}", response.body());
            } else {
                LOG.warn("Request failed: {}", response.message());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException", e.getMessage());
        }
    }

    private void addSimpleItem(SensorData data, SensComMessage message, ESensorItem item, String name) {
        if (data.hasValue(item)) {
            double value = data.getValue(item);
            message.addItem(name, value);
        }
    }

    public void scheduleProcessAttributes(Map<AppDeviceId, AttributeMap> attributes) {
        executor.execute(new CatchingRunnable(LOG, () -> processAttributes(attributes)));
    }

    private void processAttributes(Map<AppDeviceId, AttributeMap> attributes) {
        sensComIds.clear();
        attributes.forEach((dev, attr) -> processDeviceAttributes(sensComIds, dev, attr));
        sensComIds.forEach((key, value) -> LOG.info("SensCom mapping: {} -> {}", key, value));
    }

    private void processDeviceAttributes(Map<AppDeviceId, String> map, AppDeviceId appDeviceId,
            AttributeMap attributes) {
        String sensComId = attributes.getOrDefault("senscom-id", "").trim();
        if (!sensComId.isEmpty()) {
            map.put(appDeviceId, sensComId);
        }
    }

}
