package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.gls.GeoLocationConfig;
import nl.bertriksikken.mydevices.MyDevicesConfig;
import nl.bertriksikken.nbiot.NbIotConfig;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.senscom.SensComConfig;
import nl.bertriksikken.ttn.TtnConfig;

/**
 * Configuration class.
 */
public final class LoraForwarderConfig {

    @JsonProperty("ttn")
    private TtnConfig ttnConfig = new TtnConfig();

    @JsonProperty("nbiot")
    private NbIotConfig nbIotConfig = new NbIotConfig();
    
    @JsonProperty("senscom")
    private SensComConfig sensComConfig = new SensComConfig();
    
    @JsonProperty("opensense")
    private OpenSenseConfig openSenseConfig = new OpenSenseConfig();

    @JsonProperty("mydevices")
    private MyDevicesConfig myDevicesConfig = new MyDevicesConfig();

    @JsonProperty("geolocation")
    private GeoLocationConfig geoLocationConfig = new GeoLocationConfig();
    
    public TtnConfig getTtnConfig() {
        return ttnConfig;
    }

    public SensComConfig getSensComConfig() {
        return sensComConfig;
    }

    public OpenSenseConfig getOpenSenseConfig() {
        return openSenseConfig;
    }

    public NbIotConfig getNbIotConfig() {
        return nbIotConfig;
    }
    
    public MyDevicesConfig getMyDevicesConfig() {
        return myDevicesConfig;
    }

    public GeoLocationConfig getGeoLocationConfig() {
        return geoLocationConfig;
    }

}