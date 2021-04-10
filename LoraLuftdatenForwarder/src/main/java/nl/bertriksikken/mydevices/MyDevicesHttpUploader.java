package nl.bertriksikken.mydevices;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.mydevices.dto.MyDevicesMessage;
import nl.sikken.bertrik.cayenne.CayenneMessage;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Uploads data to mydevices using the HTTP method.
 */
public final class MyDevicesHttpUploader {

    private static final Logger LOG = LoggerFactory.getLogger(MyDevicesHttpUploader.class);

    private final IMyDevicesRestApi restApi;

    public static IMyDevicesRestApi newRestClient(String url, Duration timeout) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);

        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        return retrofit.create(IMyDevicesRestApi.class);
    }

    MyDevicesHttpUploader(IMyDevicesRestApi restApi) {
        this.restApi = restApi;
    }

    public void upload(CayenneMessage cayenneMessage, String userName, String password, String clientId) {
        LOG.info("Upload to mydevices.com for client {}: {}", clientId, cayenneMessage);
        MyDevicesMessage message = MyDevicesMessage.fromCayenne(cayenneMessage);
        String authToken = Credentials.basic(userName, password);
        try {
            Response<String> response = restApi.publish(authToken, clientId, message).execute();
            LOG.info("Upload to mydevices.com for client {} successful: {}", clientId, response.message());
        } catch (IOException e) {
            LOG.warn("Caught IOException: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Caught exception: ", e);
        }
    }

}
