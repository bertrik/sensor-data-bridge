package nl.bertriksikken.senscom;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Sensor.community message as uploaded through a POST, suitable for JSON serialization by Jackson.
 */
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public final class SensComMessage {

    @JsonProperty("software_version")
    private final String softwareVersion;

    @JsonProperty("sensordatavalues")
    private final List<SensComItem> items = new ArrayList<>();

    /**
     * Constructor.
     */
    public SensComMessage(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void addItem(String name, String value) {
        items.add(new SensComItem(name, value));
    }

    public void addItem(String name, Double value) {
        items.add(new SensComItem(name, value));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{softwareVersion=%s,items=%s}", softwareVersion, items);
    }

    record SensComItem(@JsonProperty("value_type") String name, @JsonProperty("value") String value) {
        /**
         * Convenience constructor.
         *
         * @param name  the item name
         * @param value the item value as double, it will be rounded to 1 decimal
         */
        public SensComItem(String name, Double value) {
            this(name, String.format(Locale.ROOT, "%.1f", value));
        }
    }

}
