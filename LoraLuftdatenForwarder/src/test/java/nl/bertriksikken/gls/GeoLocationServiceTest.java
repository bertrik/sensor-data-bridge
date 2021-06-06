package nl.bertriksikken.gls;

import java.io.IOException;
import java.time.Duration;

/**
 * Simple test case for geo location service.
 */
public final class GeoLocationServiceTest {

    public static void main(String[] args) throws IOException {
        String url = "https://location.services.mozilla.com";
        IGeoLocationRestApi restApi = GeoLocationService.newRestClient(url, Duration.ofSeconds(20));
        GeoLocationService gls = new GeoLocationService(restApi, "test");
        
        GeoLocationRequest request = new GeoLocationRequest(false);
        request.add("9C:1C:12:F6:EB:C0", -45, 1);
        request.add("9C:1C:12:F6:F5:42", -55, 1);
        GeoLocationResponse response = gls.geoLocate(request);
        System.out.println(response);
    }
    
}
