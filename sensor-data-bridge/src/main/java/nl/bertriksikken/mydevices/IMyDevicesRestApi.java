package nl.bertriksikken.mydevices;

import nl.bertriksikken.mydevices.dto.MyDevicesMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IMyDevicesRestApi {

    @POST("/things/{clientid}/data")
    Call<String> publish(@Header("Authorization") String authToken, @Path("clientid") String clientId,
            @Body MyDevicesMessage message);

}
