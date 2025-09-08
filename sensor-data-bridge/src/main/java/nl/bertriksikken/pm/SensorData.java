package nl.bertriksikken.pm;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Collection of measurement items.
 */
public final class SensorData {

    // start with a simple map containing one Number value per item
    private final Map<ESensorItem, Number> items = new LinkedHashMap<>();
    private final Instant timestamp;

    public SensorData() {
        this.timestamp = Instant.now();
    }

    public Instant getCreationTime() {
        return timestamp;
    }

    public void putValue(ESensorItem item, Number value) {
        if (value == null) {
            return;
        }
        if ((value instanceof Double) && !Double.isFinite(value.doubleValue())) {
            return;
        }
        items.put(item, value);
    }

    public boolean hasValue(ESensorItem item) {
        return items.containsKey(item);
    }

    public Double getValue(ESensorItem item) {
        return items.get(item).doubleValue();
    }

    public Number get(ESensorItem item) {
        return items.get(item);
    }

    public boolean hasValid(ESensorItem item) {
        return items.containsKey(item) && item.inRange(items.get(item).doubleValue());
    }

    @Override
    public String toString() {
        return items.entrySet().stream().map(entry -> entry.getKey().format(entry.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

}
