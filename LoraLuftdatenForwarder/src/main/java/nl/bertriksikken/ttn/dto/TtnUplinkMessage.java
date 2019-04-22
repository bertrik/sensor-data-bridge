package nl.bertriksikken.ttn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TtnUplinkMessage {
	
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

}
