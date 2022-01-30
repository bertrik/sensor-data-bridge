package nl.bertriksikken.loraforwarder;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public final class SensorDataBridgeConfigTest {

    private final ObjectMapper mapper = new YAMLMapper();

    @Test
    public void testDefaults() throws JsonProcessingException {
        SensorDataBridgeConfig config = new SensorDataBridgeConfig();
        String text = mapper.writeValueAsString(config);
        System.out.println(text);
    }

}
