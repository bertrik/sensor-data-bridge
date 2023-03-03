package nl.bertriksikken.gls;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Geo location request
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class GeoLocationResponse {

    @JsonProperty("location")
    private final GeoLocation location = new GeoLocation();

    @JsonProperty("accuracy")
    private double accuracy = Double.NaN;

    public double getLatitude() {
        return location.latitude;
    }

    public double getLongitude() {
        return location.longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{location=%s,accuracy=%.0f}", location, accuracy);
    }

    private static final class GeoLocation {

        @JsonProperty("lat")
        private double latitude = Double.NaN;

        @JsonProperty("lng")
        private double longitude = Double.NaN;

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{lat=%f,lon=%f}", latitude, longitude);
        }

    }

}
