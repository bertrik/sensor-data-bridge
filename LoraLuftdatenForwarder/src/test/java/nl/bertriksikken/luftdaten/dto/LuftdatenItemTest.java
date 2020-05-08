package nl.bertriksikken.luftdaten.dto;

import org.junit.Assert;
import org.junit.Test;

public final class LuftdatenItemTest {

    @Test
    public void testFormat() {
        Double d = 0.1 * 174;
        LuftdatenItem item = new LuftdatenItem("name", d);
        Assert.assertEquals("17.4", item.getValue());
    }
    
}
