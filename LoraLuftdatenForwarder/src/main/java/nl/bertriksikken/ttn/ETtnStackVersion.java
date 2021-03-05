package nl.bertriksikken.ttn;

/**
 * The TTN stack version, used for example to differentiate between MQTT conventions.
 */
public enum ETtnStackVersion {
    V2(""),
    V3("v3/");
    
    private final String prefix;

    ETtnStackVersion(String prefix) {
        this.prefix = prefix;
    }
    
    String getPrefix() {
        return prefix;
    }
}
