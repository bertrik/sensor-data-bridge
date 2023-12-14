package nl.bertriksikken.pm.json;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import nl.bertriksikken.pm.ESensorItem;

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
    public void testDeserialize() throws StreamReadException, DatabindException, IOException {
        JsonDecoderConfig config = MAPPER.readValue(getClass().getClassLoader().getResource("JsonDecoderConfig.yaml"),
                JsonDecoderConfig.class);
        Assert.assertEquals(1.0, config.get(0).unit, 0.1);
        Assert.assertEquals(100.0, config.get(1).unit, 0.1);
    }

}
