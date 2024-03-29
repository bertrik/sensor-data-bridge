package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.gls.GeoLocationConfig;
import nl.bertriksikken.nbiot.NbIotConfig;
import nl.bertriksikken.opensense.OpenSenseConfig;
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

    // output modules
    @JsonProperty("senscom")
    private SensComConfig sensComConfig = new SensComConfig();

    @JsonProperty("opensense")
    private OpenSenseConfig openSenseConfig = new OpenSenseConfig();

    // miscellaneous
    @JsonProperty("geolocation")
    private GeoLocationConfig geoLocationConfig = new GeoLocationConfig();

    public TtnConfig getTtnConfig() {
        return new TtnConfig(ttnConfig);
    }

    // package-private visible for test
    void setTtnConfig(TtnConfig ttnConfig) {
        this.ttnConfig = ttnConfig;
    }

    public NbIotConfig getNbIotConfig() {
        return nbIotConfig;
    }

    public SensComConfig getSensComConfig() {
        return sensComConfig;
    }

    public OpenSenseConfig getOpenSenseConfig() {
        return openSenseConfig;
    }

    public GeoLocationConfig getGeoLocationConfig() {
        return geoLocationConfig;
    }

}