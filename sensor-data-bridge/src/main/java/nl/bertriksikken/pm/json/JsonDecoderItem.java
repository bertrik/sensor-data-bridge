package nl.bertriksikken.pm.json;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.pm.ESensorItem;

/**
 * JSON decoder configuration, relates JSON items to their internal
 * representation (SI).
 */
public final class JsonDecoderItem {

    // the JSON path, e.g. "/PM10"
    @JsonProperty("path")
    String path = "";

    // the associated ESensorItem, e.g. "PM10"
    @JsonProperty("item")
    ESensorItem item;

    @SuppressWarnings("unused")
    private JsonDecoderItem() {
        // no-arg jackson constructor
        this("", null);
    }

    JsonDecoderItem(String path, ESensorItem item) {
        this.path = path;
        this.item = item;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{path=%s,item=%s}", path, item);
    }

}
