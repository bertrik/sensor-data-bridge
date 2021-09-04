package nl.bertriksikken.helium;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown =  true)
public final class HeliumUplinkMessage {

    @JsonProperty("app_eui")
    String appEui = "";
    
    @JsonProperty("dev_eui")
    String devEui = "";
    
    @JsonProperty("devaddr")
    String devAddr = "";
    
    @JsonProperty("fcnt")
    int fcnt;
    
    @JsonProperty("port")
    int port;
    
    @JsonProperty("name")
    String name = "";
    
    @JsonProperty("payload")
    byte[] payload = new byte[0];
    
    @JsonProperty("reported_at")
    long reportedAt;
    
    @JsonProperty("hotspots")
    List<HotSpot> hotSpots = new ArrayList<>();
    
    @JsonIgnoreProperties(ignoreUnknown =  true)
    static final class HotSpot {
        @JsonProperty("name")
        String name = "";
        
        @JsonProperty("lat")
        double latitude;
        
        @JsonProperty("long")
        double longitude;
        
        @JsonProperty("rssi")
        double rssi;
        
        @JsonProperty("snr")
        double snr;
    }
    
}
