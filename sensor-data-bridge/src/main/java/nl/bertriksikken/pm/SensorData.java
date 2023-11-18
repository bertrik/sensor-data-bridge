package nl.bertriksikken.pm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collection of measurement items.
 */
public final class SensorData {

    // start with a simple map containing one Number value per item
    private final Map<ESensorItem, Number> items = new LinkedHashMap<>();

    public boolean addValue(ESensorItem item, Number value) {
        if ((value instanceof Double) && !item.inRange(value.doubleValue())) {
            return false;
        }
        items.put(item, value);
        return true;
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

    @Override
    public String toString() {
        return items.toString();
    }

}
