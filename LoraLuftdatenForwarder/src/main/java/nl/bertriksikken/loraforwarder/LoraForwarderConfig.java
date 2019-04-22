package nl.bertriksikken.loraforwarder;

/**
 * Configuration class.
 */
public final class LoraForwarderConfig extends BaseConfig implements ILoraForwarderConfig {
    
	private enum EConfigItem {
        MQTT_URL("mqtt.url", "tcp://eu.thethings.network", "URL of the MQTT server"),
        MQTT_APP_ID("mqtt.appid", "particulatematter", "TTN application id"),
        MQTT_APP_KEY("mqtt.appkey", "ttn-account-v2.cNaB2zO-nRiXaCUYmSAugzm-BaG_ZSHbEc5KgHNQFsk", "TTN application access key"),

        LUFTDATEN_URL("luftdaten.url", "https://api.luftdaten.info", "luftdaten server URL (empty to disable)"),
        LUFTDATEN_TIMEOUT_MS("luftdaten.timeout", "3000", "luftdaten timeout");
		
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
    public String getMqttUrl() {
        return get(EConfigItem.MQTT_URL.key);
    }

    @Override
    public String getMqttAppId() {
        return get(EConfigItem.MQTT_APP_ID.key);
    }

    @Override
    public String getMqttAppKey() {
        return get(EConfigItem.MQTT_APP_KEY.key);
    }

	@Override
	public String getLuftdatenUrl() {
        return get(EConfigItem.LUFTDATEN_URL.key).trim();
	}

	@Override
	public int getLuftdatenTimeoutMs() {
		return Integer.parseInt(get(EConfigItem.LUFTDATEN_TIMEOUT_MS.key));
	}

}