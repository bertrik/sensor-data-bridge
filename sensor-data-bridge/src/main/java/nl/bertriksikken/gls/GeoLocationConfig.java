package nl.bertriksikken.gls;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class GeoLocationConfig {

    @JsonProperty("url")
    private String url = "https://location.services.mozilla.com";

    @JsonProperty("timeout")
    private int timeout = 30;

    @JsonProperty("apikey")
    private String apiKey = "test";

    public String getUrl() {
        return url;
    }
    
    public Duration getTimeout() {
        return Duration.ofSeconds(timeout);
    }

    public String getApiKey() {
        return apiKey;
    }

}
