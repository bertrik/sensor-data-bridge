package nl.bertriksikken.rest;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class RestApiConfig {

    @JsonProperty("url")
    private final String url;

    @JsonProperty("timeout")
    private final int timeoutSec;

    // jackson no-arg constructor
    @SuppressWarnings("unused")
    private RestApiConfig() {
        this.url = "";
        this.timeoutSec = 30;
    }

    public RestApiConfig(String url, int timeoutSec) {
        this.url = url;
        this.timeoutSec = timeoutSec;
    }

    public String getUrl() {
        return url;
    }

    public Duration getTimeout() {
        return Duration.ofSeconds(timeoutSec);
    }

}
