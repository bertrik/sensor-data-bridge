package nl.bertriksikken.gls;

import java.io.IOException;
import java.time.Duration;

/**
 * Simple test case for geo location service.
 */
public final class GeoLocationServiceTest {

    public static void main(String[] args) throws IOException {
        GeoLocationConfig config = new GeoLocationConfig();
        IGeoLocationRestApi restApi = GeoLocationService.newRestClient(config.getUrl(),
                Duration.ofSeconds(config.getTimeout()));
        GeoLocationService gls = new GeoLocationService(restApi, config.getApiKey());

        GeoLocationRequest request = new GeoLocationRequest(false);
        request.add("9C:1C:12:F6:EB:C0", -45, 1);
        request.add("9C:1C:12:F6:F5:42", -55, 1);
        GeoLocationResponse response = gls.geoLocate(request);
        System.out.println(response);
    }

}
