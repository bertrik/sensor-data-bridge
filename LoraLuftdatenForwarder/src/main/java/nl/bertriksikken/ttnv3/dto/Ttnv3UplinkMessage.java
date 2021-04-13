package nl.bertriksikken.ttnv3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import nl.bertriksikken.ttn.TtnUplinkMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Ttnv3UplinkMessage {

    @JsonProperty("end_device_ids")
    private JsonNode endDeviceIds;

    @JsonProperty("uplink_message")
    private UplinkMessage uplinkMessage;

    public TtnUplinkMessage toTtnUplinkMessage() {
        String devEui = endDeviceIds.at("/dev_eui").asText("");
        int sf = uplinkMessage.settings.at("/data_rate/lora/spreading_factor").asInt();
        return new TtnUplinkMessage(devEui, uplinkMessage.payload, uplinkMessage.fport, sf);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class UplinkMessage {
        @JsonProperty("f_port")
        private int fport;

        @JsonProperty("f_cnt")
        private int fcnt;

        @JsonProperty("frm_payload")
        private byte[] payload = new byte[0];
        
        @JsonProperty("settings")
        private JsonNode settings;
    }

}
