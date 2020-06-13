package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.luftdaten.LuftdatenConfig;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.ttn.TtnConfig;

/**
 * Configuration class.
 */
public final class LoraForwarderConfig {

    @JsonProperty("ttn")
    private TtnConfig ttnConfig = new TtnConfig();
    
    @JsonProperty("luftdaten")
    private LuftdatenConfig luftdatenConfig = new LuftdatenConfig();
    
    @JsonProperty("opensense")
    private OpenSenseConfig openSenseConfig = new OpenSenseConfig();

    public TtnConfig getTtnConfig() {
        return ttnConfig;
    }

    public LuftdatenConfig getLuftdatenConfig() {
        return luftdatenConfig;
    }

    public OpenSenseConfig getOpenSenseConfig() {
        return openSenseConfig;
    }

}