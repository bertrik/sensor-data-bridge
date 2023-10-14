package nl.bertriksikken.senscom;

import java.util.Locale;

/**
 * @see https://github.com/opendata-stuttgart/meta/wiki/EN-APIs#api-sensorcommnunity
 */
public enum ESensComPin {
    PARTICULATE_MATTER(1), // dust sensors, e.g. SDS011, PMS1003, PMS7003
    TEMPERATURE_PRESSURE(3), // temperature & pressure, but no humidity, e.g. BMP180
    TEMPERATURE_HUMIDITY(7), // temperature & humidity, but no pressure, e.g. DHT22
    POSITION(9), // WGS84
    TEMPERATURE_HUMIDITY_PRESSURE(11), // temperature & humidity & pressure, e.g. BME280
    TEMPERATURE(13), // only temperature
    NOISE(15), // noise
    NO2(17), // not implemented
    RADIATION(19); // not implemented

    private final int pin;

    ESensComPin(int pin) {
        this.pin = pin;
    }

    String getPin() {
        return String.format(Locale.ROOT, "%d", pin);
    }

    @Override
    public String toString() {
        return getPin();
    }

}
