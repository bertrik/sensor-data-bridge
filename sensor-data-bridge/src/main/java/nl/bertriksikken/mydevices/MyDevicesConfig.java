package nl.bertriksikken.mydevices;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class MyDevicesConfig {

    @JsonProperty("url")
    private String url = "https://api.mydevices.com";

    @JsonProperty("timeout")
    private int timeoutSec = 20;
    
    public String getUrl() {
        return url;
    }

    public int getTimeoutSec() {
        return timeoutSec;
    }

}
