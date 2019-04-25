package nl.bertriksikken.pm;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object for SDS011 sensor data.
 */
public final class SensorSds {

    @JsonProperty("id")
    private String id;
    @JsonProperty("PM10")
    private double pm10;
    @JsonProperty("PM2.5")
    private double pm2_5;

    private SensorSds() {
        // jackson constructor
    }

    /**
     * Constructor.
     * 
     * @param id    TODO
     * @param pm10  the PM10 value
     * @param pm2_5 the PM2.5 value
     */
    public SensorSds(String id, double pm10, double pm2_5) {
        this();
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
    }

    public String getId() {
        return id;
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{id=%s,PM10=%.1f,PM2.5=%.1f}", id, pm10, pm2_5);
    }

}
