package nl.bertriksikken.mydevices;

import nl.bertriksikken.rest.RestApiConfig;

public final class MyDevicesConfig extends RestApiConfig {

    // jackson no-arg constructor
    public MyDevicesConfig() {
        super("https://api.mydevices.com", 30);
    }

}
