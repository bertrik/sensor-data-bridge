package nl.bertriksikken.pm;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of measurement items.
 */
public final class SensorData {

    // start with a simple map containing one Double value per item
    private final Map<ESensorItem, Double> items = new HashMap<>();

    public void addValue(ESensorItem item, Double value) {
        items.put(item, value);
    }

    public boolean hasValue(ESensorItem item) {
        return items.containsKey(item);
    }

    public Double getValue(ESensorItem item) {
        return items.get(item);
    }

}
