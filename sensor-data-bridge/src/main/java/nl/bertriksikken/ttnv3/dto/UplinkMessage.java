package nl.bertriksikken.ttnv3.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#message:UplinkMessage
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UplinkMessage {
    
    @JsonProperty("f_port")
    int fport;

    @JsonProperty("f_cnt")
    int fcnt;

    @JsonProperty("frm_payload")
    byte[] frmPayload = new byte[0];

    @JsonProperty("decoded_payload")
    JsonNode decodedPayload;

    @JsonProperty("rx_metadata")
    List<JsonNode> rxMetadata = new ArrayList<>();

    @JsonProperty("settings")
    JsonNode settings;

    @JsonProperty("received_at")
    String receivedAt;

    public Instant getReceivedAt() {
        return Instant.parse(receivedAt);
    }
}
