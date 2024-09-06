package nl.bertriksikken.ttn.enddevice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#message:UpdateEndDeviceRequest
 */
@SuppressWarnings("UnusedVariable")
public final class UpdateEndDeviceRequest {

    @JsonProperty("end_device")
    private final EndDevice endDevice;

    @JsonProperty("field_mask")
    private final FieldMask fieldMask;

    UpdateEndDeviceRequest(EndDevice endDevice, FieldMask fieldMask) {
        this.endDevice = endDevice;
        this.fieldMask = fieldMask;
    }

}
