package nl.bertriksikken.ttnv3.enddevice;

import com.fasterxml.jackson.annotation.JsonProperty;

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
