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
		return rawPayload;
	}

}
