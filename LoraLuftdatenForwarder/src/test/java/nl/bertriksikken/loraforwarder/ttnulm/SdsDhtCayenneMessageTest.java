package nl.bertriksikken.loraforwarder.ttnulm;

import org.junit.Assert;
import org.junit.Test;

public final class SdsDhtCayenneMessageTest {

    @Test
    public void testHappyFlow() throws PayloadParseException {
        byte[] raw = new byte[] { 0x64, 0x02, 0x00, 0x64, 0x19, 0x02, 0x00, 0x19, 0x00, 0x67, 0x00, 0x64,
                0x01, 0x68, 0x77 };
        SdsDhtCayenneMessage msg = new SdsDhtCayenneMessage();
        msg.parse(raw);
        
        Assert.assertEquals(1.0, msg.getPm10(), 0.01);
        Assert.assertEquals(0.25, msg.getPm2_5(), 0.01);
        Assert.assertEquals(59.5, msg.getRhPerc(), 0.01);
        Assert.assertEquals(10.0, msg.getTempC(), 0.01);
    }

}
