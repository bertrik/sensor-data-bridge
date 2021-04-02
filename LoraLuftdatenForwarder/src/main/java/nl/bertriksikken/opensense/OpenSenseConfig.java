package nl.bertriksikken.opensense;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OpenSenseConfig {

    @JsonProperty("url")
    private String url = "https://api.opensensemap.org";
    
    @JsonProperty("timeout")
    private int timeoutSec = 20;
    
    public String getUrl() {
        return url;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

}
