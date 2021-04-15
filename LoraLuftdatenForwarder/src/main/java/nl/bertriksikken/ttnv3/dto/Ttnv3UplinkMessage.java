package nl.bertriksikken.ttnv3.dto;

import java.util.ArrayList;
import java.util.List;

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
        TtnUplinkMessage message = new TtnUplinkMessage(devEui, uplinkMessage.payload, uplinkMessage.fport);
        int sf = uplinkMessage.settings.at("/data_rate/lora/spreading_factor").asInt();
        double rssi = uplinkMessage.rxMetadata.stream().mapToDouble(m -> m.at("/rssi").asDouble()).max()
                .orElse(Double.NaN);
        double snr = uplinkMessage.rxMetadata.stream().mapToDouble(m -> m.at("/snr").asDouble()).max()
                .orElse(Double.NaN);
        message.setRadioParams(rssi, snr, sf);
        return message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class UplinkMessage {
        @JsonProperty("f_port")
        private int fport;

        @JsonProperty("f_cnt")
        private int fcnt;

        @JsonProperty("frm_payload")
        private byte[] payload = new byte[0];

        @JsonProperty("rx_metadata")
        private List<JsonNode> rxMetadata = new ArrayList<>();

        @JsonProperty("settings")
        private JsonNode settings;
    }

}
