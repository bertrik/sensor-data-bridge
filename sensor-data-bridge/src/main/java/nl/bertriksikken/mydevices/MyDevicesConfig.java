package nl.bertriksikken.mydevices;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class MyDevicesConfig {

    @JsonProperty("url")
    private String url = "https://api.mydevices.com";

    @JsonProperty("timeout")
    private int timeoutSec = 30;
    
    public String getUrl() {
        return url;
    }

    public Duration getTimeoutSec() {
        return Duration.ofSeconds(timeoutSec);
    }

}
