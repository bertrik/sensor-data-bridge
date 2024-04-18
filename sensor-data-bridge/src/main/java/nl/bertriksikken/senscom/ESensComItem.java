package nl.bertriksikken.senscom;

import nl.bertriksikken.pm.ESensorItem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum ESensComItem {

    // particulate matter
    PM10(ESensorItem.PM10, "P1"),
    PM4(ESensorItem.PM4_0, "P4"),
    PM2_5(ESensorItem.PM2_5,"P2"),
    PM1_0(ESensorItem.PM1_0,"P0"),
    PM10_N(ESensorItem.PM10_N,"N10"),
    PM4_0_N(ESensorItem.PM4_0_N, "N4"),
    PM2_5_N(ESensorItem.PM2_5_N,"N25"),
    PM1_0_N(ESensorItem.PM10_N,"N1"),
    PM0_5_N(ESensorItem.PM0_5_N, "N05"),
    PM_TPS(ESensorItem.PM_TPS,"TS", 3),

    // noise
    NOISE_LA_EQ(ESensorItem.NOISE_LA_EQ,"noise_LAeq"),
    NOISE_LA_MIN(ESensorItem.NOISE_LA_MIN, "noise_LA_min"),
    NOISE_LA_MAX(ESensorItem.NOISE_LA_MAX, "noise_LA_max"),

    // meteo
    TEMPERATURE(ESensorItem.TEMPERATURE, "temperature"),
    HUMIDITY(ESensorItem.HUMIDITY, "humidity"),
    PRESSURE(ESensorItem.PRESSURE, "pressure"),

    // location
    GPS_LAT(ESensorItem.POS_LAT, "lat", 5),
    GPS_LON(ESensorItem.POS_LON, "lon", 5),
    GPS_ALT(ESensorItem.POS_ALT, "height");

    private final ESensorItem item;
    private final String id;
    private final int digits;

    ESensComItem(ESensorItem item, String id) {
        // default is 1 digit
        this(item, id, 1);
    }

    ESensComItem(ESensorItem item, String id, int digits) {
        this.item = item;
        this.id = id;
        this.digits = digits;
    }

    public ESensorItem getItem() {
        return item;
    }

    public String getId() {
        return id;
    }

    public String format(Double value) {
        return BigDecimal.valueOf(value).setScale(digits, RoundingMode.HALF_UP).toString();
    }
}
