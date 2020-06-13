package nl.bertriksikken.ttn;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TtnConfig {

    @JsonProperty("mqtt_url")
    private String url = "tcp://eu.thethings.network";
    
    @JsonProperty("apps")
    private List<TtnAppConfig> apps = Arrays.asList(new TtnAppConfig());

    public String getUrl() {
        return url;
    }

    public List<TtnAppConfig> getApps() {
        return apps;
    }
    
}
