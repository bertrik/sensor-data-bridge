package nl.bertriksikken.pm;

import java.util.Locale;

/**
 * Set of meteo sensor values from a DHT22.
 */
public final class SensorDht{

    private Double temp;
    private Double rh;

    /**
     * @param temp     temperature (celcius)
     * @param rh       relative humidity (percent)
     * @param pressure (mbar or hectopascal)
     */
    public SensorDht(double temp, double rh) {
        this.temp = temp;
        this.rh = rh;
    }

    public Double getTemp() {
        return temp;
    }

    public Double getRh() {
        return rh;
    }

    public boolean hasValidTemp() {
        return (temp != null) && (temp > -100.0) && (temp < 100.0);
    }

    public boolean hasValidRh() {
        return (rh != null) && (rh >= 0) && (rh <= 100.0);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{t=%.1f,rh=%.1f}", temp, rh);
    }

}
