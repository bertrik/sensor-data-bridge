package nl.bertriksikken.opensense;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OpenSenseConfig {

    @JsonProperty("url")
    private String url = "https://api.opensensemap.org";
    
    @JsonProperty("timeout")
    private int timeoutSec = 20;
    
    @JsonProperty("ids")
    private Map<String, String> ids = new HashMap<String, String>();
    
    public OpenSenseConfig() {
        ids.put("0ttn-deveui-here", "put-opensense-boxid-here");
    }

    public String getUrl() {
        return url;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

    public Map<String, String> getIds() {
        return ids;
    }
    
}
