package nl.bertriksikken.opensense;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.luftdaten.dto.LuftdatenItem;
import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class OpenSenseUploader {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSenseUploader.class);

    private final IOpenSenseRestApi restClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<String, String> boxIds = new HashMap<>();

    public OpenSenseUploader(Map<String, String> boxIds, IOpenSenseRestApi restClient) {
        this.boxIds.putAll(boxIds);
        this.restClient = Objects.requireNonNull(restClient);
    }

    public static IOpenSenseRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        return retrofit.create(IOpenSenseRestApi.class);
    }

    public void start() {
        LOG.info("Starting OpenSenseUploader");
    }

    public void stop() {
        LOG.info("Stopping OpenSenseUploader");
        executor.shutdown();
    }

    public void scheduleUpload(String deviceId, SensorData data) {
        String boxId = boxIds.getOrDefault(deviceId, "");
        if (boxId.isBlank()) {
            return;
        }

        LuftdatenMessage message = new LuftdatenMessage("LoraLuftdatenForwarder");

        // particulate matter
        if (data.hasValue(ESensorItem.PM10)) {
            message.addItem(new LuftdatenItem("SDS_P1", data.getValue(ESensorItem.PM10)));
        }
        if (data.hasValue(ESensorItem.PM2_5)) {
            message.addItem(new LuftdatenItem("SDS_P2", data.getValue(ESensorItem.PM2_5)));
        }

        // humidity/temperature/pressure
        String meteoPrefix = getMeteoPrefix(data);
        if (data.hasValue(ESensorItem.HUMI)) {
            message.addItem(new LuftdatenItem(meteoPrefix + "humidity", data.getValue(ESensorItem.HUMI)));
        }
        if (data.hasValue(ESensorItem.TEMP)) {
            message.addItem(new LuftdatenItem(meteoPrefix + "temperature", data.getValue(ESensorItem.TEMP)));
        }
        if (data.hasValue(ESensorItem.PRESSURE)) {
            message.addItem(new LuftdatenItem(meteoPrefix + "pressure", data.getValue(ESensorItem.PRESSURE)));
        }

        // schedule upload
        String luftdatenId = "TTN-" + deviceId;
        executor.execute(() -> uploadMeasurement(boxId, luftdatenId, message));
    }

    private String getMeteoPrefix(SensorData data) {
        if (data.hasValue(ESensorItem.HUMI) && data.hasValue(ESensorItem.TEMP) && data.hasValue(ESensorItem.PRESSURE)) {
            return "BME280_";
        }
        return "";
    }

    private void uploadMeasurement(String boxId, String luftdatenId, LuftdatenMessage message) {
        LOG.info("Sending to opensense box {} for {}: {}", boxId, luftdatenId, message);
        try {
            Response<String> response = restClient.postNewMeasurements(boxId, true, message).execute();
            if (response.isSuccessful()) {
                String result = response.body();
                LOG.info("Successfully posted to opensense box {}: {}", boxId, result);
            } else {
                LOG.warn("Failed to post to opensense box {}: {}", boxId, response.errorBody().string());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Caught exception: ", e);
        }
    }

}
