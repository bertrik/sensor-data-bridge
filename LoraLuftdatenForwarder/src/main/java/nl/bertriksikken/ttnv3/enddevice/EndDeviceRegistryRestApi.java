package nl.bertriksikken.ttnv3.enddevice;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client).build();
        return retrofit.create(IEndDeviceRegistryRestApi.class);
    }

    public EndDevice getEndDeviceInfo(String applicationId, String deviceId) throws IOException {
        String mask = "attributes";
        Response<EndDevice> response = restApi.requestDeviceInfo(authToken, applicationId, deviceId, mask).execute();
        if (!response.isSuccessful()) {
            LOG.warn("Request failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }
    
}
