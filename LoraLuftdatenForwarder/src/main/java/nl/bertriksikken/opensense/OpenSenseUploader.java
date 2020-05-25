package nl.bertriksikken.opensense;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
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

    private final File configFile;
    private final IOpenSenseRestApi restClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<String, String> boxIds = new HashMap<>();

    public OpenSenseUploader(File configFile, IOpenSenseRestApi restClient) {
        this.configFile = Objects.requireNonNull(configFile);
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

        // read the config file
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
        } catch (FileNotFoundException e) {
            LOG.warn("File '{}' not found, creating default...", configFile);
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                properties.store(fos, "This file maps TTN device ids to OpenSense box ids, example: my_node=xxyyzz");
            } catch (IOException e2) {
                LOG.error("Could not write default config file, ", e2);
            }
        } catch (IOException e) {
            LOG.warn("Exception accessing config file {}: {}", configFile.getAbsoluteFile(), e.getMessage());
        }
        properties.forEach((k, v) -> boxIds.put((String) k, (String) v));
    }

    public void stop() {
        LOG.info("Stopping OpenSenseUploader uploader");
        executor.shutdown();
    }

    public void scheduleUpload(String nodeName, SensorData data) {
        String boxId = boxIds.getOrDefault(nodeName, "");
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
        if (data.hasValue(ESensorItem.HUMI)) {
            message.addItem(new LuftdatenItem("humidity", data.getValue(ESensorItem.HUMI)));
        }
        if (data.hasValue(ESensorItem.TEMP)) {
            message.addItem(new LuftdatenItem("temperature", data.getValue(ESensorItem.TEMP)));
        }
        if (data.hasValue(ESensorItem.PRESSURE)) {
            message.addItem(new LuftdatenItem("pressure", data.getValue(ESensorItem.PRESSURE)));
        }

        // schedule upload
        executor.execute(() -> uploadMeasurement(nodeName, boxId, message));
    }

    private void uploadMeasurement(String nodeName, String boxId, LuftdatenMessage message) {
        try {
            Response<String> response = restClient.postNewMeasurements(boxId, true, message).execute();
            if (response.isSuccessful()) {
                String result = response.body();
                LOG.info("Successfully posted for {} to opensense box {}: {}", nodeName, boxId, result);
            } else {
                LOG.warn("Failed to post to opensense box {}: {}", boxId, response.errorBody());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Caught exception: ", e);
        }
    }

}
