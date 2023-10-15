package nl.bertriksikken.senscom;

import nl.bertriksikken.rest.RestApiConfig;

public final class SensComConfig extends RestApiConfig {

    // jackson no-arg constructor
    public SensComConfig() {
        this("https://api.sensor.community", 30);
    }

    SensComConfig(String host, int timeout) {
        super(host, timeout);
    }

}
