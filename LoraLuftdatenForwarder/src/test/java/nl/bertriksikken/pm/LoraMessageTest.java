package nl.bertriksikken.pm;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

public final class LoraMessageTest {

    @Test
    public void testDecodeValidPartial() throws ParseException {
        byte[] data = new byte[] { 0x00, 0x10, 0x00, 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        LoraMessage msg = LoraMessage.decode(data);

        Assert.assertEquals(1.6, msg.getPm10(), 0.01);
        Assert.assertEquals(3.2, msg.getPm2_5(), 0.01);
        Assert.assertFalse(msg.getTemp().isPresent());
        Assert.assertFalse(msg.getHumidity().isPresent());
    }

    @Test(expected = ParseException.class)
    public void testDecodeEmpty() throws ParseException {
        byte[] data = new byte[] {};
        LoraMessage.decode(data);
    }

}
