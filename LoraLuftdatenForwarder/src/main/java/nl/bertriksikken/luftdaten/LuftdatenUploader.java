package nl.bertriksikken.luftdaten;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Uploader for luftdaten.info
 */
public final class LuftdatenUploader {

    private static final Logger LOG = LoggerFactory.getLogger(LuftdatenUploader.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ILuftdatenApi restClient;
    private final ExecutorService executor;

    /**
     * Constructor.
     * 
     * @param restClient the REST client
     */
    LuftdatenUploader(ILuftdatenApi restClient) {
        this.restClient = restClient;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Creates a new luftdaten REST client.
     * 
     * @param config the luftdaten config
     */
    public static LuftdatenUploader create(LuftdatenConfig config) {
        LOG.info("Creating new REST client for '{}' with timeout {}", config.getUrl(), config.getTimeout());
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeout()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        ILuftdatenApi restClient = retrofit.create(ILuftdatenApi.class);
        return new LuftdatenUploader(restClient);
    }

    private void uploadMeasurement(String sensorId, String pin, LuftdatenMessage luftdatenMessage) {
        try {
            LOG.info("Sending for {} to pin {}: '{}'", sensorId, pin, mapper.writeValueAsString(luftdatenMessage));
            Response<String> response = restClient.pushSensorData(pin, sensorId, luftdatenMessage).execute();
            if (response.isSuccessful()) {
                LOG.info("Result success: {}", response.body());
            } else {
                LOG.warn("Request failed: {}", response.message());
            }
        } catch (Exception e) {
            LOG.trace("Caught exception", e);
            LOG.warn("Caught exception: {}", e.getMessage());
        }
    }

    private void scheduleUpload(String sensorId, String pin, LuftdatenMessage message) {
        executor.execute(() -> uploadMeasurement(sensorId, pin, message));
    }

    private void addSimpleItem(SensorData data, LuftdatenMessage message, ESensorItem item, String name) {
        if (data.hasValue(item)) {
            double value = data.getValue(item);
            message.addItem(name, value);
        }
    }

    public void scheduleUpload(String deviceId, SensorData data) {
        String sensorId = "TTN-" + deviceId;

        // pin 1 (dust sensors)
        if (data.hasValue(ESensorItem.PM10) || data.hasValue(ESensorItem.PM2_5) || data.hasValue(ESensorItem.PM1_0)
                || data.hasValue(ESensorItem.PM4_0)) {
            LuftdatenMessage p1Message = new LuftdatenMessage();

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

            scheduleUpload(sensorId, "1", p1Message);
        }

        // pin 3: temperature & pressure, but no humidity
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.PRESSURE)
                && !data.hasValue(ESensorItem.HUMI)) {
            LuftdatenMessage p3Message = new LuftdatenMessage();
            p3Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p3Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            scheduleUpload(sensorId, "3", p3Message);
        }

        // pin 7: temperature & humidity, but no pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p7Message = new LuftdatenMessage();
            p7Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p7Message.addItem("humidity", data.getValue(ESensorItem.HUMI));
            scheduleUpload(sensorId, "7", p7Message);
        }

        // pin 9: position
        if (data.hasValue(ESensorItem.POS_LAT) && data.hasValue(ESensorItem.POS_LON)) {
            LuftdatenMessage p9Message = new LuftdatenMessage();
            p9Message.addItem("latitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LAT)));
            p9Message.addItem("longitude", String.format(Locale.ROOT, "%.4f", data.getValue(ESensorItem.POS_LON)));
            if (data.hasValue(ESensorItem.POS_ALT)) {
                p9Message.addItem("altitude", data.getValue(ESensorItem.POS_ALT));
            }
            scheduleUpload(sensorId, "9", p9Message);
        }

        // pin 11: temperature & humidity & pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI) && data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p11Message = new LuftdatenMessage();
            p11Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            p11Message.addItem("humidity", data.getValue(ESensorItem.HUMI));
            p11Message.addItem("pressure", data.getValue(ESensorItem.PRESSURE));
            scheduleUpload(sensorId, "11", p11Message);
        }

        // pin 13: only temperature
        if (data.hasValue(ESensorItem.TEMP) && !data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p13Message = new LuftdatenMessage();
            p13Message.addItem("temperature", data.getValue(ESensorItem.TEMP));
            scheduleUpload(sensorId, "13", p13Message);
        }

        // pin 15: sound not implemented
        // pin 17: NO2 not implemented
        // pin 19: radiation not implemented
    }

    public void start() {
        LOG.info("Starting Luftdaten.info uploader");
    }

    public void stop() {
        LOG.info("Stopping Luftdaten.info uploader");
        executor.shutdown();
    }

}
