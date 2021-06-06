package nl.bertriksikken.gls;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class GeoLocationService {

    private static final Logger LOG = LoggerFactory.getLogger(GeoLocationService.class);

    private final IGeoLocationRestApi restClient;
    private final String key;

    /**
     * Constructor.
     * 
     * @param restClient the REST client
     * @param key the API key
     */
    public GeoLocationService(IGeoLocationRestApi restClient, String key) {
        this.restClient = restClient;
        this.key = key;
    }

    public static IGeoLocationRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        return retrofit.create(IGeoLocationRestApi.class);
    }

    /**
     * Performs a WiFi geo-location (blocking).
     * 
     * @param request containing the WiFi AP information
     * @return the response with the result
     * @throws IOException
     */
    public GeoLocationResponse geoLocate(GeoLocationRequest request) throws IOException {
        Response<GeoLocationResponse> response = restClient.geoLocate(key, request).execute();
        if (response.isSuccessful()) {
            LOG.info("Result success: {}", response.body());
        } else {
            LOG.warn("Request failed: {}", response.message());
        }
        return response.body();
    }

}
