package nl.bertriksikken.gls;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.rest.RestApiConfig;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class GeoLocationConfig extends RestApiConfig {

    @JsonProperty("apikey")
    private String apiKey = "test";

    // jackson no-arg constructor
    public GeoLocationConfig() {
        super("https://location.services.mozilla.com", 30);
    }
    
    public String getApiKey() {
        return apiKey;
    }

}
