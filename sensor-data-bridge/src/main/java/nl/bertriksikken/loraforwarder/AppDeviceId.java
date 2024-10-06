package nl.bertriksikken.loraforwarder;

/**
 * Combination of application name and device id that uniquely identifies a device.<br>
 * This class has a hashCode() and equals() implementation that allows it to be used in set and maps.
 */
public record AppDeviceId(String appName, String deviceId) {

}
