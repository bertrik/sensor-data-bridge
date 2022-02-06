package nl.bertriksikken.senscom;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class SensComConfig {

    @JsonProperty("url")
    private String url = "https://api.sensor.community";

    @JsonProperty("timeout")
    private int timeout = 30;

    public String getUrl() {
        return url;
    }

    public Duration getTimeout() {
        return Duration.ofSeconds(timeout);
    }

}
