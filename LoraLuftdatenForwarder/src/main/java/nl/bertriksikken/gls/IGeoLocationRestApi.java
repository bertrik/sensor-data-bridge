package nl.bertriksikken.gls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * See https://ichnaea.readthedocs.io/en/latest/api/geolocate.html
 */
public interface IGeoLocationRestApi {

    @POST("/v1/geolocate")
    public Call<GeoLocationResponse> geoLocate(@Query("key") String key, @Body GeoLocationRequest request);
    
}
