package nl.bertriksikken.helium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ArrayRecordComponent")
@JsonIgnoreProperties(ignoreUnknown = true)
public record HeliumUplinkMessage(
        @JsonProperty("app_eui") String appEui,
        @JsonProperty("dev_eui") String devEui,
        @JsonProperty("devaddr") String devAddr, // device address with bytes in reverse order
        @JsonProperty("fcnt") int fcnt,
        @JsonProperty("port") int port,
        @JsonProperty("name") String name,
        @JsonProperty("payload") byte[] payload,
        @JsonProperty("reported_at") long reportedAt, // milliseconds
        @JsonProperty("hotspots") List<HotSpot> hotSpots) {
    public HeliumUplinkMessage {
        appEui = Objects.requireNonNullElse(appEui, "");
        devEui = Objects.requireNonNullElse(devEui, "");
        devAddr = Objects.requireNonNullElse(devAddr, "");
        name = Objects.requireNonNullElse(name, "");
        payload = Objects.requireNonNullElse(payload.clone(), new byte[0]);
        hotSpots = Objects.requireNonNullElse(hotSpots, new ArrayList<>());
    }

    @Override
    public byte[] payload() {
        return payload.clone();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record HotSpot(
            @JsonProperty("name") String name,
            @JsonProperty("lat") double latitude,
            @JsonProperty("long") double longitude,
            @JsonProperty("rssi") double rssi,
            @JsonProperty("snr") double snr) {
        HotSpot {
            name = Objects.requireNonNullElse(name, "");
        }
    }

}
