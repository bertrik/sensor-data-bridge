package nl.bertriksikken.ttnv3.enddevice;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class EndDeviceRegistryRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(EndDeviceRegistryRestApi.class);

    private final IEndDeviceRegistryRestApi restApi;
    private final String authToken;

    public EndDeviceRegistryRestApi(IEndDeviceRegistryRestApi restApi, String apiKey) {
        this.restApi = restApi;
        this.authToken = "Bearer " + apiKey;
    }

    public static IEndDeviceRegistryRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        return retrofit.create(IEndDeviceRegistryRestApi.class);
    }

    public EndDevice getEndDeviceInfo(String applicationId, String deviceId) throws IOException {
        String fields = IEndDeviceRegistryRestApi.FIELD_ATTRIBUTES;
        Response<EndDevice> response = restApi.requestDeviceInfo(authToken, applicationId, deviceId, fields).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

    public List<EndDevice> listEndDevices(String applicationId) throws IOException {
        String fields = String.join(",", IEndDeviceRegistryRestApi.FIELD_IDS,
                IEndDeviceRegistryRestApi.FIELD_ATTRIBUTES);
        Response<EndDevices> response = restApi.listEndDevices(authToken, applicationId, fields).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        EndDevices endDevices = response.body();
        return endDevices.getEndDevices();
    }

}
