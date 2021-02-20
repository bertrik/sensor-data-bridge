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
    
    // values to be passed to the fieldMask parameter
    public static final String FIELD_IDS = "ids";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ATTRIBUTES = "attributes";
    public static final String FIELD_VERSION_IDS = "version_ids";

    @GET("/api/v3/applications/{application_id}/devices/{device_id}")
    Call<EndDevice> requestDeviceInfo(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Path("device_id") String deviceId,
            @Query("field_mask") String fieldMask);
    
    @GET("/api/v3/applications/{application_id}/devices")
    Call<EndDevices> listEndDevices(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Query("field_mask") String fieldMask);

}
