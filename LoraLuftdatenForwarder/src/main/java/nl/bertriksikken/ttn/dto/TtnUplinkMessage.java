package nl.bertriksikken.ttn.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TtnUplinkMessage {

    @JsonProperty("app_id")
    String appId;

    @JsonProperty("dev_id")
    String devId;

    @JsonProperty("hardware_serial")
    String hardwareSerial;

    @JsonProperty("port")
    int port;

    @JsonProperty("counter")
    int counter;

    @JsonProperty("payload_raw")
    byte[] rawPayload;

    @JsonProperty("payload_fields")
    Map<String, Object> payloadFields;
    
    public String getAppId() {
        return appId;
    }

    public String getDevId() {
        return devId;
    }

    public String getHardwareSerial() {
        return hardwareSerial;
    }

    public int getPort() {
        return port;
    }

    public int getCounter() {
        return counter;
    }

    public byte[] getRawPayload() {
        return rawPayload.clone();
    }

    public Map<String, Object> getPayloadFields() {
    	return new HashMap<>(payloadFields);
    }
    
}
