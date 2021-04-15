package nl.bertriksikken.ttn;

import org.junit.Assert;
import org.junit.Test;

public final class TtnUplinkMessageTest {
    
    @Test
    public void testString() {
        TtnUplinkMessage message = new TtnUplinkMessage("deviceui", new byte[] {1, 2, 3}, 1, 7);
        Assert.assertNotNull(message.toString());
    }

}
