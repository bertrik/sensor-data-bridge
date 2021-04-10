package nl.bertriksikken.loraforwarder;

import java.util.HashMap;
import java.util.Map;

public final class AttributeMap extends HashMap<String, String> {
    
    private static final long serialVersionUID = 1L;

    AttributeMap(Map<String, String> map) {
        super(map);
    }
    
}
