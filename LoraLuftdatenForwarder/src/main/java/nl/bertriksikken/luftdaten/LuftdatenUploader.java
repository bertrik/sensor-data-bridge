package nl.bertriksikken.luftdaten;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.dto.LuftdatenItem;
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

    private static final String SOFTWARE_VERSION = "https://github.com/bertrik/LoraLuftdatenForwarder";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ILuftdatenApi restClient;
    private final ExecutorService executor;

    /**
     * Constructor.
     * 
     * @param restClient the REST client
     */
    public LuftdatenUploader(ILuftdatenApi restClient) {
        this.restClient = restClient;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Creates a new REST client.
     * 
     * @param url     the URL of the server, e.g. "https://api.luftdaten.info"
     * @param timeout the timeout
     * @return a new REST client.
     */
    public static ILuftdatenApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        return retrofit.create(ILuftdatenApi.class);
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

    public void scheduleUpload(String deviceId, SensorData data) {
        String sensorId = "TTN-" + deviceId;

        // pin 1 (dust sensors)
        if (data.hasValue(ESensorItem.PM10) || data.hasValue(ESensorItem.PM2_5) || data.hasValue(ESensorItem.PM1_0)) {
            LuftdatenMessage p1Message = new LuftdatenMessage(SOFTWARE_VERSION);
            if (data.hasValue(ESensorItem.PM10)) {
                p1Message.addItem(new LuftdatenItem("P1", data.getValue(ESensorItem.PM10)));
            }
            if (data.hasValue(ESensorItem.PM2_5)) {
                p1Message.addItem(new LuftdatenItem("P2", data.getValue(ESensorItem.PM2_5)));
            }
            if (data.hasValue(ESensorItem.PM1_0)) {
                p1Message.addItem(new LuftdatenItem("P0", data.getValue(ESensorItem.PM1_0)));
            }
            scheduleUpload(sensorId, "1", p1Message);
        }

        // pin 3: temperature & pressure, but no humidity
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.PRESSURE)
                && !data.hasValue(ESensorItem.HUMI)) {
            LuftdatenMessage p3Message = new LuftdatenMessage(SOFTWARE_VERSION);
            p3Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
            p3Message.addItem(new LuftdatenItem("pressure", data.getValue(ESensorItem.PRESSURE)));
            scheduleUpload(sensorId, "3", p3Message);
        }

        // pin 7: temperature & humidity, but no pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p7Message = new LuftdatenMessage(SOFTWARE_VERSION);
            p7Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
            p7Message.addItem(new LuftdatenItem("humidity", data.getValue(ESensorItem.HUMI)));
            scheduleUpload(sensorId, "7", p7Message);
        }

        // pin 9: position
        if (data.hasValue(ESensorItem.POS_LAT) && data.hasValue(ESensorItem.POS_LON)) {
            LuftdatenMessage p9Message = new LuftdatenMessage(SOFTWARE_VERSION);
            p9Message.addItem(new LuftdatenItem("latitude", String.format("%.4f", data.getValue(ESensorItem.POS_LAT))));
            p9Message.addItem(new LuftdatenItem("longitude", String.format("%.4f", data.getValue(ESensorItem.POS_LON))));
            if (data.hasValue(ESensorItem.POS_ALT)) {
                p9Message.addItem(new LuftdatenItem("altitude", data.getValue(ESensorItem.POS_ALT)));
            }
            scheduleUpload(sensorId, "9", p9Message);
        }

        // pin 11: temperature & humidity & pressure
        if (data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.HUMI) && data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p11Message = new LuftdatenMessage(SOFTWARE_VERSION);
            p11Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
            p11Message.addItem(new LuftdatenItem("humidity", data.getValue(ESensorItem.HUMI)));
            p11Message.addItem(new LuftdatenItem("pressure", data.getValue(ESensorItem.PRESSURE)));
            scheduleUpload(sensorId, "11", p11Message);
        }

        // pin 13: only temperature
        if (data.hasValue(ESensorItem.TEMP) && !data.hasValue(ESensorItem.HUMI)
                && !data.hasValue(ESensorItem.PRESSURE)) {
            LuftdatenMessage p13Message = new LuftdatenMessage(SOFTWARE_VERSION);
            p13Message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
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
