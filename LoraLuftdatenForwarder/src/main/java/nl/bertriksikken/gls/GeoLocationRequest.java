package nl.bertriksikken.gls;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeoLocationRequest {

    @JsonProperty("considerIp")
    private final boolean considerIp;
    
    @JsonProperty("wifiAccessPoints")
    private final List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>();
 
    public GeoLocationRequest(boolean considerIp) {
        this.considerIp = considerIp;
    }
    
    public void add(String macAddress, int signalStrength, int channel) {
        wifiAccessPoints.add(new WifiAccessPoint(macAddress, signalStrength, channel));
    }
    
    private final class WifiAccessPoint {
        @JsonProperty("macAddress")
        private final String macAddress;
        
        @JsonProperty("signalStrength")
        private final int signalStrength;
        
        @JsonProperty("channel")
        private final int channel;
        
        WifiAccessPoint(String macAddress, int signalStrength, int channel) {
            this.macAddress = macAddress;
            this.signalStrength = signalStrength;
            this.channel = channel;
        }
    }
}
