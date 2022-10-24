package nl.bertriksikken.nbiot;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class NbIotConfig {

    @JsonProperty("port")
    private int port = 9000;

    public int getPort() {
        return port;
    }

}
