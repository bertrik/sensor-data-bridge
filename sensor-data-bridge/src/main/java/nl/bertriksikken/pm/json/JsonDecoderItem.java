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

    // the unit of the item, e.g. 100 for hectopascal (mbar)
    @JsonProperty("unit")
    double unit = 1.0;

    @SuppressWarnings("unused")
    private JsonDecoderItem() {
        // no-arg jackson constructor
        this("", null, 1.0);
    }

    public JsonDecoderItem(String path, ESensorItem item, double unit) {
        this.path = path;
        this.item = item;
        this.unit = unit;
    }

    public JsonDecoderItem(String path, ESensorItem item) {
        this(path, item, 1.0);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{path=%s,item=%s,unit=%f}", path, item, unit);
    }

}
