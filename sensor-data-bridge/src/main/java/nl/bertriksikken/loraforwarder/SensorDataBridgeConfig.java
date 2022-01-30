package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.gls.GeoLocationConfig;
import nl.bertriksikken.mydevices.MyDevicesConfig;
import nl.bertriksikken.nbiot.NbIotConfig;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.pm.json.JsonDecoderConfig;
import nl.bertriksikken.senscom.SensComConfig;
import nl.bertriksikken.ttn.TtnConfig;

/**
 * Configuration class.
 */
public final class SensorDataBridgeConfig {

    // input modules
    @JsonProperty("ttn")
    private TtnConfig ttnConfig = new TtnConfig();

    @JsonProperty("nbiot")
    private NbIotConfig nbIotConfig = new NbIotConfig();
    
    // decoders
    @JsonProperty("json")
    private JsonDecoderConfig jsonDecoderConfig = new JsonDecoderConfig();
    
    // output modules
    @JsonProperty("senscom")
    private SensComConfig sensComConfig = new SensComConfig();
    
    @JsonProperty("opensense")
    private OpenSenseConfig openSenseConfig = new OpenSenseConfig();

    @JsonProperty("mydevices")
    private MyDevicesConfig myDevicesConfig = new MyDevicesConfig();

    // miscellaneous
    @JsonProperty("geolocation")
    private GeoLocationConfig geoLocationConfig = new GeoLocationConfig();
    
    public TtnConfig getTtnConfig() {
        return ttnConfig;
    }

    public NbIotConfig getNbIotConfig() {
        return nbIotConfig;
    }
    
    public JsonDecoderConfig getJsonDecoderConfig() {
        return new JsonDecoderConfig(jsonDecoderConfig);
    }
    
    public SensComConfig getSensComConfig() {
        return sensComConfig;
    }

    public OpenSenseConfig getOpenSenseConfig() {
        return openSenseConfig;
    }

    public MyDevicesConfig getMyDevicesConfig() {
        return myDevicesConfig;
    }

    public GeoLocationConfig getGeoLocationConfig() {
        return geoLocationConfig;
    }

}