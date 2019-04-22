package nl.bertriksikken.pm;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a message received from the MQTT stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SensorMessage {

    @JsonProperty("SDS011")
    private SensorSds sds;
    
    @JsonProperty("bme280")
    private SensorBme bme;
    
    private SensorMessage() {
        // Jackson constructor
    }
    
    /**
     * Constructor.
     * 
     * @param bme meteo sensor value
     */
    public SensorMessage(SensorBme bme) {
        this();
        this.bme = bme;
    }
    
    public SensorSds getSds() {
    	return sds;
    }
    
    public SensorBme getBme() {
        return bme;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{sds=%s,bme=%s}", sds, bme);
    }
    
}
