package nl.bertriksikken.ttn.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TtnUplinkMetaData {
    
    @JsonProperty("gateways")
    private final List<TtnUplinkGateway> gateways = new ArrayList<>();
    
    public List<TtnUplinkGateway> getGateways() {
        return gateways;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class TtnUplinkGateway {
        @JsonProperty("gtw_id")
        String id = "";
        
        @JsonProperty("rssi")
        double rssi = Double.NaN;
        
        @JsonProperty("snr")
        double snr = Double.NaN;
        
        public String getId() {
            return id;
        }

        public double getRssi() {
            return rssi;
        }

        public double getSnr() {
            return snr;
        }
    }
    
}
