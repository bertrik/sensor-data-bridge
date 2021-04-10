package nl.bertriksikken.ttnv3.enddevice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EndDevice {
    
    @JsonProperty
    private final DeviceIds ids;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    @JsonProperty("attributes")
    private final Map<String, String> attributes = new HashMap<>();

    public EndDevice() {
        this.ids = new DeviceIds();
        String creationTime = Instant.now().toString();
        this.createdAt = creationTime;
        this.updatedAt = creationTime;
    }
    
    public DeviceIds getIds() {
        return ids;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{%s,%s}", ids, attributes);
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DeviceIds {
        @JsonProperty("device_id")
        private final String deviceId;

        @JsonProperty("dev_eui")
        private final String devEui;
        
        @JsonProperty("join_eui")
        private final String joinEui;
        
        DeviceIds(String deviceId, String devEui, String joinEui) {
            this.deviceId = deviceId;
            this.devEui = devEui;
            this.joinEui = joinEui;
        }

        private DeviceIds() {
            // jackson constructor
            this("", "", "");
        }
        
        public String getDeviceId() {
            return deviceId;
        }

        public String getDevEui() {
            return devEui;
        }

        public String getJoinEui() {
            return joinEui;
        }
        
        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{%s,%s}", deviceId, devEui);
        }
    }
    
}
