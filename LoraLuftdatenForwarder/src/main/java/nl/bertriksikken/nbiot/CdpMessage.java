package nl.bertriksikken.nbiot;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object, as used in the POST message by the CDP.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CdpMessage {

    @JsonProperty("reports")
    public List<Report> reports = new ArrayList<>();

    public final static class Report {
        
        @JsonProperty("serialNumber")
        public String serialNumber = "";
        
        @JsonProperty("timestamp")
        public long timestamp = 0;
        
        @JsonProperty("subscriptionId")
        public String subscriptionId = "";
        
        @JsonProperty("resourcePath")
        public String resourcePath = "";
        
        @JsonProperty("value")
        public String value = "";
    }
}
