package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.loraforwarder.EPayloadEncoding;

public final class TtnAppConfig {

    @JsonProperty("name")
    private String name = "particulatematter";

    @JsonProperty("key")
    private String key = "ttn-account-v2.cNaB2zO-nRiXaCUYmSAugzm-BaG_ZSHbEc5KgHNQFsk";

    @JsonProperty("encoding")
    private EPayloadEncoding encoding = EPayloadEncoding.CAYENNE;

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
