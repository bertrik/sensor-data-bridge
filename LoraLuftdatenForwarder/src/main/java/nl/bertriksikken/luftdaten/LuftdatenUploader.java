package nl.bertriksikken.luftdaten;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.luftdaten.dto.LuftdatenItem;
import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.pm.SensorMessage;
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
	
	// the "PIN" we upload dust data to
	private static final String PIN = "1";

	private final ObjectMapper mapper = new ObjectMapper();
	private final ILuftdatenApi restClient;
	private final String softwareVersion;

	/**
	 * Constructor.
	 * 
	 * @param restClient the REST client
	 * @param softwareVersion the software version
	 */
	public LuftdatenUploader(ILuftdatenApi restClient, String softwareVersion) {
		this.restClient = restClient;
		this.softwareVersion = softwareVersion;
	}
	
	/**
	 * Creates a new REST client.
	 * 
	 * @param url the URL of the server, e.g. "https://api.luftdaten.info"
	 * @param timeout the timeout
	 * @return a new REST client.
	 */
	public static ILuftdatenApi newRestClient(String url, Duration timeout) {
		OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(timeout)
				.build();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(url)
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create())
				.client(client)
				.build();
		
		return retrofit.create(ILuftdatenApi.class);
	}
	
    public void uploadMeasurement(String sensorId, SensorMessage message) {
    	LuftdatenMessage luftDatenMessage = new LuftdatenMessage(softwareVersion);
    	luftDatenMessage.addItem(new LuftdatenItem("P1", message.getSds().getPm10()));
    	luftDatenMessage.addItem(new LuftdatenItem("P2", message.getSds().getPm2_5()));
    	try {
    		LOG.info("Sending for {} to pin {}: '{}'", sensorId, PIN, mapper.writeValueAsString(luftDatenMessage));
            Response<String> response = restClient.pushSensorData(PIN, sensorId, luftDatenMessage).execute();
    		if (response.isSuccessful()) {
    			LOG.info("Result success: {}", response.body());
    		} else {
    			LOG.warn("Request failed: {}", response.message());
    		}
    	} catch (IOException e) {
    		LOG.warn("Caught exception '{}'", e.getMessage());
    	}
    }

	public void start() {
		LOG.info("Starting Luftdaten.info uploader");
	}

	public void stop() {
		LOG.info("Stopping Luftdaten.info uploader");
	}
	
}
