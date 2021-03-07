package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.loraforwarder.EPayloadEncoding;

public final class TtnAppConfig {
    @JsonProperty("name")
    private String name = "particulatematter";

    @JsonProperty("version")
    private ETtnStackVersion version = ETtnStackVersion.V3;
    
    @JsonProperty("key")
    private String key = "NNSXS.LHD22PFZMI3B7WF6FDWIK45N4244U7DWRVWZASI.XXXXXX";

    @JsonProperty("encoding")
    private EPayloadEncoding encoding = EPayloadEncoding.CAYENNE;

    public ETtnStackVersion getVersion() {
        return version;
    }
    
    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public EPayloadEncoding getEncoding() {
        return encoding;
    }

}
