package nl.bertriksikken.pm;

import java.util.Locale;

public enum ESensorItem {
    PM10("Particulate matter PM10 (aka P1)", "ug/m3", 0),
    PM2_5("Particulate matter PM2.5 (aka P2)", "ug/m3", 0),
    PM4_0("Particulate matter PM4 (aka P4)", "ug/m3", 0),
    PM1_0("Particulate matter PM1.0 (aka P0)", "ug/m3", 0),

    PM10_N("Particulate matter PM10", "#/cm3", 0),
    PM4_0_N("Particulate matter PM4", "#/cm3", 0),
    PM2_5_N("Particulate matter PM2.5", "#/cm3", 0),
    PM1_0_N("Particulate matter PM1.0", "#/cm3", 0),
    PM0_5_N("Particulate matter PM0.5", "#/cm3", 0),
    
    PM_TPS("Typical particle size", "um"),

    HUMIDITY("Relative humidity", "%", 0, 100),
    TEMPERATURE("Temperature", "degC", -100, 100),
    PRESSURE("Atmospheric pressure", "Pa", 0, 1E6),

    POS_LAT("Latitude", "deg", -90, 90),
    POS_LON("Longitude", "deg", -180, 180),
    POS_ALT("Altitude", "m"),
    
    NOISE_LA_EQ("Noise avg", "dBA", 0, 200),
    NOISE_LA_MIN("Noise min", "dBA", 0, 200),
    NOISE_LA_MAX("Noise max", "dBA", 0, 200),
    
    NO2("NO2 concentration", "?", 0),
    RADIATION("Radiation", "?", 0),
    
    LORA_SF("Spreading factor", "", 6, 12),
    LORA_SNR("Signal-to-noise ratio", "dB"),
    LORA_RSSI("Signal strength", "dBm");

    private String description;
    private String unit;
    private double minValue;
    private double maxValue;

    private ESensorItem(String description, String unit, double minValue, double maxValue) {
        this.description = description;
        this.unit = unit;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    private ESensorItem(String description, String unit, double minValue) {
        this(description, unit, minValue, Double.POSITIVE_INFINITY);
    }

    private ESensorItem(String description, String unit) {
        this(description, unit, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s (%s)", description, unit);
    }

    public boolean inRange(Double value) {
        return Double.isFinite(value) && (value >= minValue) && (value <= maxValue);
    }

    public String format(Number value) {
        return String.format(Locale.ROOT, "%s=%s%s", name(), value, unit);
    }
}
