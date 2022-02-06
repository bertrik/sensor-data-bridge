package nl.bertriksikken.opensense;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class OpenSenseConfig {

    @JsonProperty("url")
    private String url = "https://api.opensensemap.org";
    
    @JsonProperty("timeout")
    private int timeoutSec = 30;
    
    public String getUrl() {
        return url;
    }

    public Duration getTimeoutSec() {
        return Duration.ofSeconds(timeoutSec);
    }

}
