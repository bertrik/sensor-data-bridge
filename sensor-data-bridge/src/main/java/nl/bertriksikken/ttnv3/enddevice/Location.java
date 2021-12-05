package nl.bertriksikken.ttnv3.enddevice;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Location {

    public static final String SOURCE_REGISTRY = "SOURCE_REGISTRY";

    @JsonProperty("source")
    String source = "";

    @JsonProperty("latitude")
    double latitude = Double.NaN;

    @JsonProperty("longitude")
    double longitude = Double.NaN;

    @JsonProperty("altitude")
    int altitude = 0;

    private Location() {
        // jackson constructor
    }
    
    public Location(double latitude, double longitude) {
        this(SOURCE_REGISTRY, latitude, longitude, 0);
    }
    
    Location(String source, double latitude, double longitude, int altitude) {
        this();
        this.source = source;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%f,%f,%d,%s}", latitude, longitude, altitude, source);
    }
    
}
