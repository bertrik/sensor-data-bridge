package nl.bertriksikken.gls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

public final class GeoLocationConfigTest {

    /**
     * Verifies that object can be serialized as YAML.
     */
    @Test
    public void testSerialize() throws JsonProcessingException {
        GeoLocationConfig config = new GeoLocationConfig();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String serialized = objectMapper.writeValueAsString(config);
        System.out.println(serialized);
    }
    
}
