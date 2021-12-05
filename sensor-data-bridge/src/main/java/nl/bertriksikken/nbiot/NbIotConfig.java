package nl.bertriksikken.nbiot;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class NbIotConfig {

    @JsonProperty("port")
    private int port = 9000;

    @JsonProperty("path")
    private String path = "";

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

}
