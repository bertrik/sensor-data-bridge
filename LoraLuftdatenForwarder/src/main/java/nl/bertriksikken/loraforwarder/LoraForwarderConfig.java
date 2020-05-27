package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.time.Duration;

/**
 * Configuration class.
 */
public final class LoraForwarderConfig extends BaseConfig implements ILoraForwarderConfig {

    private enum EConfigItem {
        NODE_ENCODING("node.encoding", "cayenne", "The payload encoding"),

        TTN_MQTT_URL("ttn.mqtt.url", "tcp://eu.thethings.network", "TTN MQTT server URL"),
        TTN_APP_ID("ttn.app.id", "particulatematter", "TTN application id"),
        TTN_APP_KEY("ttn.app.key", "ttn-account-v2.cNaB2zO-nRiXaCUYmSAugzm-BaG_ZSHbEc5KgHNQFsk",
                "TTN application access key"),

        LUFTDATEN_URL("luftdaten.url", "https://api.sensor.community", "luftdaten server URL (empty to disable)"),
        LUFTDATEN_TIMEOUT_MS("luftdaten.timeout", "10000", "luftdaten API timeout (milliseconds)"),

        OPENSENSE_URL("opensense.url", "https://api.opensensemap.org", "OpenSense server URL (empty to disable)"),
        OPENSENSE_TIMEOUT_MS("opensense.timeout", "10000", "OpenSense API timeout (milliseconds)"),
        OPENSENSE_CONFIG_FILE("opensense.configfile", "opensense_boxids.properties", "OpenSense config file name");

        private final String key, value, comment;

        private EConfigItem(String key, String defValue, String comment) {
            this.key = key;
            this.value = defValue;
            this.comment = comment;
        }
    }

    /**
     * Constructor.
     */
    public LoraForwarderConfig() {
        for (EConfigItem e : EConfigItem.values()) {
            add(e.key, e.value, e.comment);
        }
    }

    @Override
    public String getNodeEncoding() {
        return get(EConfigItem.NODE_ENCODING.key).trim();
    }
    
    @Override
    public String getTtnMqttUrl() {
        return get(EConfigItem.TTN_MQTT_URL.key);
    }

    @Override
    public String getTtnAppId() {
        return get(EConfigItem.TTN_APP_ID.key);
    }

    @Override
    public String getTtnAppKey() {
        return get(EConfigItem.TTN_APP_KEY.key);
    }

    @Override
    public String getLuftdatenUrl() {
        return get(EConfigItem.LUFTDATEN_URL.key).trim();
    }
    
    @Override
    public Duration getLuftdatenTimeout() {
        return Duration.ofMillis(Integer.parseInt(get(EConfigItem.LUFTDATEN_TIMEOUT_MS.key)));
    }

    @Override
    public String getOpenSenseUrl() {
        return get(EConfigItem.OPENSENSE_URL.key).trim();
    }

    @Override
    public Duration getOpenSenseTimeout() {
        return Duration.ofMillis(Integer.parseInt(get(EConfigItem.OPENSENSE_TIMEOUT_MS.key)));
    }

    @Override
    public File getOpenSenseConfigFile() {
        return new File(get(EConfigItem.OPENSENSE_CONFIG_FILE.key));
    }

}