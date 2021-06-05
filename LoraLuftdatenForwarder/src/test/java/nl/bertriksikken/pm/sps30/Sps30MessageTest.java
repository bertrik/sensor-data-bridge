package nl.bertriksikken.pm.sps30;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.pm.PayloadParseException;

public final class Sps30MessageTest {

    @Test(expected = PayloadParseException.class)
    public void testEmptyData() throws PayloadParseException {
        Sps30Message.parse(new byte[] {});
    }
    
    @Test
    public void testZeroData() throws PayloadParseException {
        Sps30Message message = Sps30Message.parse(new byte[20]);
        Assert.assertNotNull(message);
    }
}
