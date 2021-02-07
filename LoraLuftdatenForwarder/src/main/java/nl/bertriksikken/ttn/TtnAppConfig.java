package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.loraforwarder.EPayloadEncoding;

public final class TtnAppConfig {
    @JsonProperty("name")
    private String name = "particulatematter";

    @JsonProperty("version")
    private ETtnStackVersion version = ETtnStackVersion.V2;
    
    @JsonProperty("key")
    private String key = "ttn-account-v2.cNaB2zO-nRiXaCUYmSAugzm-BaG_ZSHbEc5KgHNQFsk";

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
