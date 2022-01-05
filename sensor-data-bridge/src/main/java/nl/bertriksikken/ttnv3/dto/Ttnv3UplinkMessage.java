package nl.bertriksikken.ttnv3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import nl.bertriksikken.ttn.TtnUplinkMessage;

/**
 * https://www.thethingsindustries.com/docs/reference/data-formats/#uplink-messages
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Ttnv3UplinkMessage {

    @JsonProperty("end_device_ids")
    private JsonNode endDeviceIds;

    @JsonProperty("uplink_message")
    private UplinkMessage uplinkMessage;

    public TtnUplinkMessage toTtnUplinkMessage() {
        String devId = endDeviceIds.at("/device_id").asText("");
        String appId = endDeviceIds.at("/application_ids/application_id").asText("");
        String devEui = endDeviceIds.at("/dev_eui").asText("");
        String decodedPayload = uplinkMessage.decodedPayload != null ? uplinkMessage.decodedPayload.toString() : "";
        TtnUplinkMessage message = new TtnUplinkMessage(appId, devId, devEui, uplinkMessage.frmPayload, decodedPayload,
                uplinkMessage.fport);
        int sf = uplinkMessage.settings.at("/data_rate/lora/spreading_factor").asInt();
        double rssi = uplinkMessage.rxMetadata.stream().mapToDouble(m -> m.at("/rssi").asDouble()).max()
                .orElse(Double.NaN);
        double snr = uplinkMessage.rxMetadata.stream().mapToDouble(m -> m.at("/snr").asDouble()).max()
                .orElse(Double.NaN);
        message.setRadioParams(rssi, snr, sf);
        return message;
    }

}
