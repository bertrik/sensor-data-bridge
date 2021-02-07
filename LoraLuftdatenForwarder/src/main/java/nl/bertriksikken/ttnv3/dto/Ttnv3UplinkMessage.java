package nl.bertriksikken.ttnv3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Ttnv3UplinkMessage {

    @JsonProperty("end_device_ids")
    EndDeviceIds endDeviceIds;
    
    @JsonProperty("received_at")
    String receivedAt;
    
    @JsonProperty("uplink_message")
    UplinkMessage uplinkMessage;
    
    public EndDeviceIds getEndDeviceIds() {
        return endDeviceIds;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public UplinkMessage getUplinkMessage() {
        return uplinkMessage;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class EndDeviceIds {
        @JsonProperty("device_id")
        String deviceId;
        
        @JsonProperty("dev_eui")
        String deviceEui;

        @JsonProperty("join_eui")
        String joinEui;

        @JsonProperty("dev_addr")
        String deviceAddress;

        public String getDeviceEui() {
            return deviceEui;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class UplinkMessage {
        @JsonProperty("f_port")
        int fport;
        
        @JsonProperty("f_cnt")
        int fcnt;
        
        @JsonProperty("frm_payload")
        byte[] payload = new byte[0];

        public byte[] getPayload() {
            return payload.clone();
        }
    }
    
}
