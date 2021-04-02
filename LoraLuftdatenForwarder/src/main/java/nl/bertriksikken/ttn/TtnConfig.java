package nl.bertriksikken.ttn;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TtnConfig {

    @JsonProperty("mqtt_url")
    private String mqttUrl = "tcp://eu1.cloud.thethings.network";
    
    @JsonProperty("identity_server_url")
    private String identityServerUrl = "https://eu1.cloud.thethings.network";
    
    @JsonProperty("identity_server_timeout")
    private int identityServerTimeout = 20;
    
    @JsonProperty("apps")
    private List<TtnAppConfig> apps = Arrays.asList(new TtnAppConfig());

    public String getMqttUrl() {
        return mqttUrl;
    }

    public String getIdentityServerUrl() {
        return identityServerUrl;
    }
    
    public long getIdentityServerTimeout() {
        return identityServerTimeout;
    }
    public List<TtnAppConfig> getApps() {
        return apps;
    }

    
}
