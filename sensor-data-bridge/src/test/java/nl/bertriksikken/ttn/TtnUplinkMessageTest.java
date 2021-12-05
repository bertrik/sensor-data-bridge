package nl.bertriksikken.ttn;

import org.junit.Assert;
import org.junit.Test;

public final class TtnUplinkMessageTest {
    
    @Test
    public void testString() {
        TtnUplinkMessage message = new TtnUplinkMessage("app", "device", "eui", new byte[] {1, 2, 3}, "", 1);
        Assert.assertNotNull(message.toString());
    }

}
