package nl.bertriksikken.opensense.dto;

import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SenseBox {

    @JsonProperty("name")
    private String name;

    @JsonProperty("sensors")
    private List<Sensor> sensors;

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{name=%s,sensors=%s}", name, sensors);
    }

}
