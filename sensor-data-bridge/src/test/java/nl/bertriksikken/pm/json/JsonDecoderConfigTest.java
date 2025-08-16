package nl.bertriksikken.pm.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import nl.bertriksikken.pm.ESensorItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JsonDecoderConfigTest {

    private static final YAMLMapper MAPPER = new YAMLMapper();

    @Test
    public void testSerialize() throws JsonProcessingException {
        JsonDecoderConfig config = new JsonDecoderConfig();
        config.add(new JsonDecoderItem("/temperature", ESensorItem.TEMPERATURE));
        config.add(new JsonDecoderItem("/pressure", ESensorItem.PRESSURE, 100));
        String yaml = MAPPER.writeValueAsString(config);
        System.out.println(yaml);
    }

    @Test
    public void testDeserialize() throws IOException {
        JsonDecoderConfig config = MAPPER.readValue(getClass().getClassLoader().getResource("JsonDecoderConfig.yaml"),
                JsonDecoderConfig.class);
        assertEquals(1.0, config.get(0).unit, 0.1);
        assertEquals(100.0, config.get(1).unit, 0.1);
    }

}
