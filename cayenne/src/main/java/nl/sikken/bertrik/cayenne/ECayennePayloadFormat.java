package nl.sikken.bertrik.cayenne;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * See https://community.mydevices.com/t/cayenne-lpp-2-0/7510
 */
public enum ECayennePayloadFormat {

    DYNAMIC_SENSOR_PAYLOAD(1), //
    PACKED_SENSOR_PAYLOAD(2), //
    FULL_SCALE_GPS_PAYLOAD(3), //

    ACTUATOR_COMMANDS(10), //
    DEVICE_PERIOD_CONFIGURATION(11), //
    SENSOR_PERIOD_CONFIGURATION(13), //
    SENSOR_ENABLE_CONFIGURATION(14); //

    private int port;

    ECayennePayloadFormat(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    private static Map<Integer, ECayennePayloadFormat> MAP = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(v -> MAP.put(v.port, v));
    }

    static ECayennePayloadFormat fromPort(int port) {
        return MAP.get(port);
    }

}
