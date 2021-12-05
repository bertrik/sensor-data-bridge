package nl.bertriksikken.senscom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sensor.community message as uploaded through a POST.
 */
public final class SensComMessage {

    private static final String SOFTWARE_VERSION = "https://github.com/bertrik/LoraLuftdatenForwarder";

    @JsonProperty("software_version")
    private String softwareVersion;

    @JsonProperty("sensordatavalues")
    private final List<SensComItem> items = new ArrayList<>();

    /**
     * Constructor.
     */
    public SensComMessage() {
        this.softwareVersion = SOFTWARE_VERSION;
    }

    public void addItem(String name, String value) {
        items.add(new SensComItem(name, value));
    }

    public void addItem(String name, Double value) {
        items.add(new SensComItem(name, value));
    }

    public List<SensComItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{softwareVersion=%s,items=%s}", softwareVersion, items);
    }

}
