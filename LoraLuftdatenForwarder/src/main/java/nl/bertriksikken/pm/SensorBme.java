package nl.bertriksikken.pm;

import java.util.Locale;

/**
 * Set of meteo sensor values from a BME280.
 */
public final class SensorBme {

    private Double temp;
    private Double rh;
    private Double pressure;

    /**
     * @param temp     temperature (celcius)
     * @param rh       relative humidity (percent)
     * @param pressure (mbar or hectopascal)
     */
    public SensorBme(double temp, double rh, double pressure) {
        this.temp = temp;
        this.rh = rh;
        this.pressure = pressure;
    }

    public Double getTemp() {
        return temp;
    }

    public Double getRh() {
        return rh;
    }

    public Double getPressure() {
        return pressure;
    }

    public boolean hasValidTemp() {
        return (temp != null) && (temp > -100.0) && (temp < 100.0);
    }

    public boolean hasValidRh() {
        return (rh != null) && (rh >= 0) && (rh <= 100.0);
    }

    public boolean hasValidPressure() {
        return (pressure != null) && (pressure > 800.0) && (pressure < 1200.0);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{t=%.1f,rh=%.1f,p=%.1f}", temp, rh, pressure);
    }

}
