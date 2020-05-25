package nl.bertriksikken.opensense.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Measurement {

    @JsonProperty("value")
    private String value;

    @JsonProperty("createdAt")
    private String createdAt;

    public String getValue() {
        return value;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s", value);
    }

}
