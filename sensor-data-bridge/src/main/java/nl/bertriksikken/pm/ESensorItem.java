package nl.bertriksikken.pm;

public enum ESensorItem {

    PM10("Particulate matter PM10 (aka P1), in ug/m3", 0),
    PM2_5("Particulate matter PM2.5 (aka P2), in ug/m3", 0),
    PM4_0("Particulate matter PM4 (aka P4), in ug/m3", 0),
    PM1_0("Particulate matter PM1.0 (aka P0), in ug/m3", 0),
    
    PM10_N("Particulate matter PM10, in #/cm3", 0),
    PM4_0_N("Particulate matter PM4, in #/cm3", 0),
    PM2_5_N("Particulate matter PM2.5, in #/cm3", 0),
    PM1_0_N("Particulate matter PM1.0, in #/cm3", 0),
    PM0_5_N("Particulate matter PM0.5, in #/cm3", 0),
    
    PM_TPS("Typical particle size, in um"),

    HUMI("Relative humidity, in percent", 0, 100),
    TEMP("Temperature, in degrees Celcius", -100, 100),
    PRESSURE("Atmospheric pressure, in Pa", 0, 1E6),

    POS_LAT("Latitude in degrees", -90, 90),
    POS_LON("Longitude in degrees", -180, 180),
    POS_ALT("Altitude in meters"),
    
    NOISE_LA_EQ("Noise avg (dBA)", 0, 200),
    NOISE_LA_MIN("Noise min (dBA)", 0, 200),
    NOISE_LA_MAX("Noise max (dBA)", 0, 200),
    
    NO2("NO2 concentration (unit?)", 0),
    RADIATION("Radiation (unit?)", 0),
    
    LORA_SF("Spreading factor", 6, 12),
    LORA_SNR("Signal-to-noise ratio"),
    LORA_RSSI("Signal strength, in dBm");

    private double minValue;
    private double maxValue;
    private String description;

    private ESensorItem(String description, double minValue, double maxValue) {
        this.description = description;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    private ESensorItem(String description, double minValue) {
        this(description, minValue, Double.POSITIVE_INFINITY);
    }

    private ESensorItem(String description) {
        this(description, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public String getDescription() {
        return description;
    }

    public boolean inRange(Double value) {
        return Double.isFinite(value) && (value >= minValue) && (value <= maxValue);
    }
}
