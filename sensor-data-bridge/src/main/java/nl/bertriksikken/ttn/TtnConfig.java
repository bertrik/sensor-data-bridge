package nl.bertriksikken.ttn;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class TtnConfig {

    @JsonProperty("mqtt_url")
    private String mqttUrl = "tcp://eu1.cloud.thethings.network";

    @JsonProperty("identity_server_url")
    private String identityServerUrl = "https://eu1.cloud.thethings.network";

    @JsonProperty("identity_server_timeout")
    private int identityServerTimeout = 30;

    @JsonProperty("apps")
    private List<TtnAppConfig> apps = new ArrayList<>();

    // default no-arg constructor
    public TtnConfig() {
    }

    // copy constructor
    public TtnConfig(TtnConfig original) {
        this.mqttUrl = original.mqttUrl;
        this.identityServerUrl = original.identityServerUrl;
        this.identityServerTimeout = original.identityServerTimeout;
        this.apps = List.copyOf(original.apps);
    }

    public void addApp(TtnAppConfig app) {
        apps.add(app);
    }

    public String getMqttUrl() {
        return mqttUrl;
    }

    public String getIdentityServerUrl() {
        return identityServerUrl;
    }

    public Duration getIdentityServerTimeout() {
        return Duration.ofSeconds(identityServerTimeout);
    }

    public List<TtnAppConfig> getApps() {
        return List.copyOf(apps);
    }

}
