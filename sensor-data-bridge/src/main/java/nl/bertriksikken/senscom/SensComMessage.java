package nl.bertriksikken.senscom;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sensor.community message as uploaded through a POST, suitable for JSON serialization by Jackson.
 */
public final class SensComMessage {

    @JsonProperty("software_version")
    private String softwareVersion;

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

    public List<SensComItem> getItems() {
        return List.copyOf(items);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{softwareVersion=%s,items=%s}", softwareVersion, items);
    }
    
    static final class SensComItem {

        @JsonProperty("value_type")
        private String name;
        @JsonProperty("value")
        private String value;

        private SensComItem() {
            // jackson constructor
        }

        /**
         * Constructor.
         * 
         * @param name  the item name
         * @param value the item value
         */
        SensComItem(String name, String value) {
            this();
            this.name = name;
            this.value = value;
        }

        /**
         * Convenience constructor.
         * 
         * @param name the item name
         * @param value the item value as double, it will be rounded to 1 decimal
         */
        public SensComItem(String name, Double value) {
            this(name, String.format(Locale.ROOT, "%.1f", value));
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{name=%s,value=%s}", name, value);
        }

    }

}
