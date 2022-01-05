package nl.bertriksikken.ttnv3.enddevice;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.ttn.TtnAppConfig;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Communicates with the TTN v3 device registry API.<br>
 * <br>
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#the-enddeviceregistry-service
 */
public final class EndDeviceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(EndDeviceRegistry.class);

    private final IEndDeviceRegistryRestApi restApi;
    private final String applicationId;
    private final String authToken;

    EndDeviceRegistry(IEndDeviceRegistryRestApi restApi, String applicationId, String apiKey) {
        this.restApi = restApi;
        this.applicationId = applicationId;
        this.authToken = "Bearer " + apiKey;
    }

    public EndDevice buildEndDevice(String deviceId) {
        return new EndDevice(applicationId, deviceId);
    }

    public static EndDeviceRegistry create(String url, int timeout, TtnAppConfig config) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(Duration.ofSeconds(timeout)).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        IEndDeviceRegistryRestApi restApi = retrofit.create(IEndDeviceRegistryRestApi.class);
        return new EndDeviceRegistry(restApi, config.getName(), config.getKey());
    }

    public EndDevice getEndDevice(String deviceId, List<String> fields) throws IOException {
        String fieldMask = String.join(",", fields);
        Response<EndDevice> response = restApi.getEndDevice(authToken, applicationId, deviceId, fieldMask).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

    public List<EndDevice> listEndDevices() throws IOException {
        String fields = String.join(",", IEndDeviceRegistryRestApi.FIELD_IDS,
                IEndDeviceRegistryRestApi.FIELD_ATTRIBUTES);
        Response<EndDevices> response = restApi.listEndDevices(authToken, applicationId, fields).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        EndDevices endDevices = response.body();
        return endDevices.getEndDevices();
    }

    public EndDevice updateEndDevice(EndDevice endDevice, List<String> fields) throws IOException {
        FieldMask fieldMask = new FieldMask(fields);
        UpdateEndDeviceRequest updateEndDeviceRequest = new UpdateEndDeviceRequest(endDevice, fieldMask);
        Response<EndDevice> response = restApi
                .updateEndDevice(authToken, applicationId, endDevice.getIds().getDeviceId(), updateEndDeviceRequest)
                .execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

}
