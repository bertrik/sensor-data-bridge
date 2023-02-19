package nl.bertriksikken.ttnv3.enddevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.ttnv3.dto.UplinkMessage;

/**
 * Representation of<br>
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#message:EndDevice
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class EndDevice {

    public static final String LOCATION_USER = "user";

    @JsonProperty("ids")
    private DeviceIds ids = new DeviceIds();

    @JsonProperty("attributes")
    private final Map<String, String> attributes = new HashMap<>();

    @JsonProperty("locations")
    private final Map<String, Location> locations = new HashMap<>();
    
    @JsonProperty("mac_state")
    private final MACState macState = new MACState();

    private EndDevice() {
        // jackson constructor
    }

    public EndDevice(String applicationId, String deviceId) {
        this();
        this.ids = new DeviceIds(applicationId, deviceId);
    }

    public String getDeviceId() {
        return ids.getDeviceId();
    }
    
    public DeviceIds getIds() {
        return ids;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }

    public void setLocation(String type, Location location) {
        locations.put(type, location);
    }

    public Map<String, Location> getLocations() {
        return new HashMap<>(locations);
    }

    public MACState getMACState() {
        return macState;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,%s}", ids, attributes);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DeviceIds {
        @JsonProperty("device_id")
        private String deviceId = "";

        @JsonProperty("dev_eui")
        private String devEui = "";

        @JsonProperty("application_ids")
        private final Map<String, String> applicationIds = new HashMap<>();

        DeviceIds(String applicationId, String deviceId) {
            this();
            applicationIds.put("application_id", applicationId);
            this.deviceId = deviceId;
        }

        // jackson constructor
        private DeviceIds() {
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getDevEui() {
            return devEui;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{%s,%s}", deviceId, devEui);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MACState {
        @JsonProperty("recent_uplinks")
        private List<UplinkMessage> recentUplinks = new ArrayList<>();
    
        public List<UplinkMessage> getRecentUplinks() {
            return List.copyOf(recentUplinks);
        }
    }

}
