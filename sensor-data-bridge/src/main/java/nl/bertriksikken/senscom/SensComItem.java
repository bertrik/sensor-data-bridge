package nl.bertriksikken.senscom;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One sensor.community measurement item.
 */
public final class SensComItem {

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
    SensComItem(String name, Double value) {
        this(name, String.format(Locale.ROOT, "%.1f", value));
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{name=%s,value=%s}", name, value);
    }

}
