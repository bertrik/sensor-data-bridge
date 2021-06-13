package nl.bertriksikken.mydevices;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.mydevices.dto.MyDevicesMessage;
import nl.bertriksikken.pm.SensorData;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Uploads data to myDevices using the HTTP method. <br>
 * See https://developers.mydevices.com/cayenne/docs/cayenne-mqtt-api/
 * #cayenne-mqtt-api-overview-using-mqtt-with-cayenne-option-3-use-http-to-push-mqtt-data
 */
public final class MyDevicesHttpUploader {

    private static final Logger LOG = LoggerFactory.getLogger(MyDevicesHttpUploader.class);

    private static final String KEY_USERNAME = "mydevices-username";
    private static final String KEY_PASSWORD = "mydevices-password";
    private static final String KEY_CLIENTID = "mydevices-clientid";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final IMyDevicesRestApi restApi;

    private final Map<String, MyDevicesCredentials> credentialMap = new HashMap<>();

    MyDevicesHttpUploader(IMyDevicesRestApi restApi) {
        this.restApi = restApi;
    }

    public static MyDevicesHttpUploader create(MyDevicesConfig config) {
        LOG.info("Creating new REST client for '{}' with timeout {}", config.getUrl(), config.getTimeoutSec());

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(config.getTimeoutSec()))
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        IMyDevicesRestApi restApi = retrofit.create(IMyDevicesRestApi.class);
        return new MyDevicesHttpUploader(restApi);
    }

    public void scheduleUpload(String deviceId, SensorData sensorData) {
        MyDevicesCredentials credentials = credentialMap.get(deviceId);
        if (credentials != null) {
            MyDevicesMessage message = MyDevicesMessage.fromSensorData(sensorData);
            executor.execute(() -> uploadMeasurement(deviceId, credentials, message));
        }
    }

    void uploadMeasurement(String deviceId, MyDevicesCredentials credentials, MyDevicesMessage message) {
        LOG.info("Upload to myDevices for client {}: {}", credentials.clientId, message);
        String authToken = Credentials.basic(credentials.user, credentials.pass);
        Response<String> response;
        try {
            response = restApi.publish(authToken, credentials.clientId, message).execute();
            if (response.isSuccessful()) {
                String result = response.body();
                LOG.info("Upload to myDevices for {} to client {} success: {}", deviceId, credentials.clientId, result);
            } else {
                LOG.warn("Upload to myDevices for {} to client {} failure: {}", deviceId, credentials.clientId,
                        response.errorBody().string());
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException: {}", e.getMessage());
        }
    }

    public void processAttributes(Map<String, AttributeMap> attributes) {
        Map<String, MyDevicesCredentials> map = new HashMap<>();
        attributes.forEach((dev, attr) -> processDeviceAttributes(map, dev, attr));
        credentialMap.clear();
        credentialMap.putAll(map);
        credentialMap.forEach((device, c) -> LOG.info("myDevices mapping: {} -> {}", device, c.clientId));
    }

    private void processDeviceAttributes(Map<String, MyDevicesCredentials> map, String device, AttributeMap attr) {
        if (attr.containsKey(KEY_USERNAME) && attr.containsKey(KEY_PASSWORD) && attr.containsKey(KEY_CLIENTID)) {
            map.put(device,
                    new MyDevicesCredentials(attr.get(KEY_USERNAME), attr.get(KEY_PASSWORD), attr.get(KEY_CLIENTID)));
        }
    }

}
