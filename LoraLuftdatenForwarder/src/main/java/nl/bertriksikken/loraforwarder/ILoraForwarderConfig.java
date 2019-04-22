package nl.bertriksikken.loraforwarder;

/**
 * Configuration interface for the application.
 */
public interface ILoraForwarderConfig {

    /**
     * @return the URL of the MQTT server
     */
    String getMqttUrl();
    
    /**
     * @return the MQTT topic to listen on
     */
    String getMqttTopic();
    
	/**
	 * @return the URL of the luftdaten.info API
	 */
	String getLuftdatenUrl();
	
	/**
	 * @return timeout (ms) for accessing the luftdaten.info API
	 */
	int getLuftdatenTimeoutMs();
	
}
