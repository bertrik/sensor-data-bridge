package nl.bertriksikken.senscom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.senscom.SensComMessage.SensComItem;
import org.junit.Assert;
import org.junit.Test;

public final class SensComMessageTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerializeSensComItem() throws JsonProcessingException {
        Double d = 0.1 * 174;
        SensComItem item = new SensComItem("name", d);
        String json = MAPPER.writeValueAsString(item);
        Assert.assertEquals("{\"value_type\":\"name\",\"value\":\"17.4\"}", json);
    }

    @Test
    public void testSerialize() throws JsonProcessingException {
        SensComMessage message = new SensComMessage("version");
        message.addItem("string", "svalue");
        message.addItem("double", "dvalue");
        String json = MAPPER.writeValueAsString(message);
        Assert.assertEquals("{\"software_version\":\"version\",\"sensordatavalues\":[" +
                "{\"value_type\":\"string\",\"value\":\"svalue\"},{\"value_type\":\"double\",\"value\":\"dvalue\"}]}", json);
    }

}
