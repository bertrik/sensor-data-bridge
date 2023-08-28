package nl.bertriksikken.senscom;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.senscom.SensComMessage.SensComItem;

public final class SensComItemTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    public void testFormat() throws JsonProcessingException {
        Double d = 0.1 * 174;
        SensComItem item = new SensComItem("name", d);
        String json = MAPPER.writeValueAsString(item);
        Assert.assertEquals("{\"value_type\":\"name\",\"value\":\"17.4\"}", json);
    }
    
}
