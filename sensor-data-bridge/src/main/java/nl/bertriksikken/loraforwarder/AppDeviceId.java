package nl.bertriksikken.loraforwarder;

import java.util.Locale;
import java.util.Objects;

/**
 * Combination of application name and device id that uniquely identifies a device.<br>
 * This class has a hashCode() and equals() implementation that allows it to be used in set and maps.
 */
public final class AppDeviceId {

    private final String appName;
    private final String deviceId;

    public AppDeviceId(String appName, String deviceId) {
        this.appName = appName;
        this.deviceId = deviceId;
    }
    
    public String getAppName() {
        return appName;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof AppDeviceId) {
            AppDeviceId other = (AppDeviceId) object;
            return appName.equals(other.appName) && deviceId.equals(other.deviceId);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(appName, deviceId);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s/%s", appName, deviceId);
    }
    
}
