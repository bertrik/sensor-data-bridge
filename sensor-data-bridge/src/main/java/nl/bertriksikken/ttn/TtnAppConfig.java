package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import nl.bertriksikken.loraforwarder.EPayloadEncoding;

public final class TtnAppConfig {
    @JsonProperty("name")
    private String name = "particulatematter";

    @JsonProperty("key")
    private String key = "NNSXS.7TC4U3WRNZ765GEEUMHUKUOJ4KTCFLP5ZW3XHFY.YRCQKPSFO636OS4CRALMH5JOPS3F4SO7OPKIY3YOGA37JDKWBFTA";

    @JsonProperty("decoder")
    private DecoderConfig decoder = new DecoderConfig();

    // jackson no-arg constructor
    public TtnAppConfig() {
    }

    public TtnAppConfig(String name, String key, DecoderConfig decoder) {
        this.name = name;
        this.key = key;
        this.decoder = decoder;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public DecoderConfig getDecoder() {
        return decoder;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DecoderConfig {
        @JsonProperty("encoding")
        private EPayloadEncoding encoding = EPayloadEncoding.CAYENNE;

        @JsonProperty("properties")
        private JsonNode properties = TextNode.valueOf("");

        // jackson no-arg constructor
        DecoderConfig() {
        }

        public DecoderConfig(EPayloadEncoding encoding, JsonNode properties) {
            this.encoding = encoding;
            this.properties = properties;
        }

        public EPayloadEncoding getEncoding() {
            return encoding;
        }

        public JsonNode getProperties() {
            return properties;
        }
    }

}
