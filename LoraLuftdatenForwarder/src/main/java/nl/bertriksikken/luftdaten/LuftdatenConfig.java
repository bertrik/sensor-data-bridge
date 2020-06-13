package nl.bertriksikken.luftdaten;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class LuftdatenConfig {

    @JsonProperty("url")
    private String url = "https://api.sensor.community";

    @JsonProperty("timeout")
    private int timeout = 20;

    public String getUrl() {
        return url;
    }

    public int getTimeout() {
        return timeout;
    }

}
