package nl.bertriksikken.senscom;

import nl.bertriksikken.rest.RestApiConfig;

public final class SensComConfig extends RestApiConfig {

    // jackson no-arg constructor
    public SensComConfig() {
        super("https://api.sensor.community", 30);
    }

}
