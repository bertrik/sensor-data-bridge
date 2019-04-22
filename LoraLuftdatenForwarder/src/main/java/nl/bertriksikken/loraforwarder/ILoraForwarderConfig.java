package nl.bertriksikken.loraforwarder;

/**
 * Configuration interface for the application.
 */
public interface ILoraForwarderConfig {

    /**
     * @return the URL of the MQTT server
     */
    String getMqttUrl();
    String getMqttAppId();
    String getMqttAppKey();
    
	/**
	 * @return the URL of the luftdaten.info API
	 */
	String getLuftdatenUrl();
	
	/**
	 * @return timeout (ms) for accessing the luftdaten.info API
	 */
	int getLuftdatenTimeoutMs();
	
}
