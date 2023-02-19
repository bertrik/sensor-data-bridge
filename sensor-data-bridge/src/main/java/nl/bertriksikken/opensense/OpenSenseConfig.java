package nl.bertriksikken.opensense;

import nl.bertriksikken.rest.RestApiConfig;

public final class OpenSenseConfig extends RestApiConfig {
    
    // jackson no-arg constructor
    public OpenSenseConfig() {
        super("https://api.opensensemap.org", 30);
    }

}
