package nl.bertriksikken.ttnv3.enddevice;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class UpdateEndDeviceRequest {

    @JsonProperty("end_device")
    private EndDevice endDevice;
    
    @JsonProperty("field_mask")
    private FieldMask fieldMask;
    
    UpdateEndDeviceRequest(EndDevice endDevice, FieldMask fieldMask) {
        this.endDevice = endDevice;
        this.fieldMask = fieldMask;
    }
    
}
