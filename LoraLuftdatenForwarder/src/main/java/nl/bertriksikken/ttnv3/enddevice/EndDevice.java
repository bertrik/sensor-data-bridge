package nl.bertriksikken.ttnv3.enddevice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EndDevice {

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    private final Map<String, String> attributes = new HashMap<>();

    public EndDevice() {
        String creationTime = Instant.now().toString();
        this.createdAt = creationTime;
        this.updatedAt = creationTime;
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
    
}
