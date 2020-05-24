package nl.bertriksikken.opensense;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.opensense.dto.SenseBox;

public final class OpenSenseApiTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenSenseApiTest.class);

    public static void main(String[] args) throws IOException {
        String url = "https://api.opensensemap.org";
        IOpenSenseRestApi restApi = OpenSenseUploader.newRestClient(url, Duration.ofSeconds(10));
        
        // perform a get on a box
        String boxId = "5eca4a0fd0545b001c0e5d46";
        SenseBox senseBox = restApi.getBox(boxId).execute().body();
        LOG.info("{}", senseBox);
    }
    
    
}
