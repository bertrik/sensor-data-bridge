package nl.bertriksikken.opensense.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Sensor {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("lastMeasurement")
    private Measurement lastMeasurement;

    @JsonProperty("sensorType")
    private String sensorType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("unit")
    private String unit;

    public String getId() {
        return id;
    }

    public Measurement getLastMeasurement() {
        return lastMeasurement;
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getTitle() {
        return title;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{id=%s,sensorType=%s,title=%s,unit=%s,measurement=%s}", id, sensorType,
                title, unit, lastMeasurement);
    }

}
