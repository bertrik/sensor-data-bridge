package nl.bertriksikken.ttnv3.enddevice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST API for TTN device registry, https://www.thethingsindustries.com/docs/reference/api/end_device/
 */
public interface IEndDeviceRegistryRestApi {
    
    // values to be passed to the fieldMask parameter
    public static final String FIELD_IDS = "ids";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ATTRIBUTES = "attributes";
    public static final String FIELD_VERSION_IDS = "version_ids";

    // end device registry
    @GET("/api/v3/applications/{application_id}/devices")
    Call<EndDevices> listEndDevices(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Query("field_mask") String fieldMask);

    @GET("/api/v3/applications/{application_id}/devices/{device_id}")
    Call<EndDevice> getEndDevice(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Path("device_id") String deviceId,
            @Query("field_mask") String fieldMask);

    @PUT("/api/v3/applications/{application_id}/devices/{device_id}")
    Call<EndDevice> updateEndDevice(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Path("device_id") String deviceId,
            @Body UpdateEndDeviceRequest request);

    // name server device registry
    @GET("/api/v3/ns/applications/{application_id}/devices/{device_id}")
    Call<EndDevice> getNsEndDevice(@Header("Authorization") String authToken,
            @Path("application_id") String applicationId, @Path("device_id") String deviceId,
            @Query("field_mask") String fieldMask);
    
}
