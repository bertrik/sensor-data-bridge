package nl.bertriksikken.opensense;

import nl.bertriksikken.luftdaten.dto.LuftdatenMessage;
import nl.bertriksikken.opensense.dto.SenseBox;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IOpenSenseRestApi {

    @GET("/boxes/{boxid}")
    Call<SenseBox> getBox(@Path("boxid") String boxId);

    @POST("/boxes/{boxid}/data")
    Call<String> postNewMeasurements(@Path("boxid") String boxId, @Query("luftdaten") boolean isLuftDaten,
            @Body LuftdatenMessage message);

}
