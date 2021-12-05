package nl.bertriksikken.gls;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeoLocationConfig {

    @JsonProperty("url")
    private String url = "https://location.services.mozilla.com";

    @JsonProperty("timeout")
    private int timeout = 20;

    @JsonProperty("apikey")
    private String apiKey = "test";

    public String getUrl() {
        return url;
    }
    
    public int getTimeout() {
        return timeout;
    }

    public String getApiKey() {
        return apiKey;
    }

}
