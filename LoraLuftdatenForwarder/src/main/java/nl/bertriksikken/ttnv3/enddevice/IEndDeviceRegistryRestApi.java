package nl.bertriksikken.ttnv3.enddevice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST API for TTN device registry
 */
public interface IEndDeviceRegistryRestApi {

    @GET("/api/v3/applications/{application_id}/devices/{device_id}")
    Call<EndDevice> requestDeviceInfo(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Path("device_id") String deviceId,
            @Query("field_mask") String fieldMask);

}
