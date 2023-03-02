package nl.bertriksikken.pm.json;

import java.util.ArrayList;

public final class JsonDecoderConfig extends ArrayList<JsonDecoderItem> {

    public JsonDecoderConfig(JsonDecoderConfig config) {
        super(config);
    }

    public JsonDecoderConfig() {
        super();
    }

    private static final long serialVersionUID = 1L;

}
