package nl.bertriksikken.pm;

public enum ESensorItem {

    PM10("Particulate matter PM10 (aka P1), in ug/m3"),
    PM2_5("Particulate matter PM2.5 (aka P2), in ug/m3"), 
    PM4_0("Particulate matter PM4 (aka P4), in ug/m3"), 
    PM1_0("Particulate matter PM1.0 (aka P0), in ug/m3"),
    
    PM10_N("Particulate matter PM10, in #/cm3"),
    PM4_0_N("Particulate matter PM4, in #/cm3"), 
    PM2_5_N("Particulate matter PM2.5, in #/cm3"), 
    PM1_0_N("Particulate matter PM1.0, in #/cm3"),
    PM0_5_N("Particulate matter PM0.5, in #/cm3"),
    
    PM_TPS("Typical particle size, in um"),

    HUMI("Relative humidity, in percent"),
    TEMP("Temperature, in degrees Celcius"),
    PRESSURE("Atmospheric pressure, in Pa"),

    POS_LAT("Latitude in degrees"),
    POS_LON("Longitude in degrees"),
    POS_ALT("Altitude in meters"),
    
    SOUND("Sound pressure (unit?)"),
    
    NO2("NO2 concentration (unit?)"),
    RADIATION("Radiation (unit?)"), 
    
    LORA_SF("Spreading factor"),
    LORA_SNR("Signal-to-noise ratio"),
    LORA_RSSI("Signal strength, in dBm");

    private String description;

    private ESensorItem(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
