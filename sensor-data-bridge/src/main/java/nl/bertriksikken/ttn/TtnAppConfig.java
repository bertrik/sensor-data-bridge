package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.loraforwarder.EPayloadEncoding;

public final class TtnAppConfig {
    @JsonProperty("name")
    private String name = "particulatematter";

    @JsonProperty("key")
    private String key = "NNSXS.7TC4U3WRNZ765GEEUMHUKUOJ4KTCFLP5ZW3XHFY.YRCQKPSFO636OS4CRALMH5JOPS3F4SO7OPKIY3YOGA37JDKWBFTA";

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
