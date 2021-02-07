package nl.bertriksikken.ttn;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TtnConfig {

    @JsonProperty("mqtt_url_v2")
    private String urlV2 = "tcp://eu.thethings.network";
    
    @JsonProperty("mqtt_url_v3")
    private String urlV3 = "tcp://eu1.cloud.thethings.network";
    
    @JsonProperty("apps")
    private List<TtnAppConfig> apps = Arrays.asList(new TtnAppConfig());

    public String getUrlV2() {
        return urlV2;
    }

    public String getUrlV3() {
        return urlV3;
    }

    public List<TtnAppConfig> getApps() {
        return apps;
    }
    
}
