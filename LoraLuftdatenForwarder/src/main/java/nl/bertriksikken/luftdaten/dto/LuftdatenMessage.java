package nl.bertriksikken.luftdaten.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Luftdaten message as uploaded through a POST.
 */
public final class LuftdatenMessage {

    @JsonProperty("software_version")
    private String softwareVersion;

    @JsonProperty("sensordatavalues")
    private final List<LuftdatenItem> items = new ArrayList<>();

    private LuftdatenMessage() {
        // jackson constructor
    }

    /**
     * Constructor.
     * 
     * @param softwareVersion the software version
     */
    public LuftdatenMessage(String softwareVersion) {
        this();
        this.softwareVersion = softwareVersion;
    }

    public void addItem(LuftdatenItem item) {
        items.add(item);
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public List<LuftdatenItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{softwareVersion=%s,items=%s}", softwareVersion, items);
    }

}
