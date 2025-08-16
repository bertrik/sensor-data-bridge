package nl.bertriksikken.pm.cayenne;

import nl.bertriksikken.pm.PayloadParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TtnCayenneMessageTest {

    @Test
    public void testHappyFlow() throws PayloadParseException {
        byte[] raw = new byte[]{0x01, 0x02, 0x00, 0x64, 0x02, 0x02, 0x00, 0x19, 0x03, 0x67, (byte) 0xFF, (byte) 0xF0,
                0x04, 0x68, 0x77, 0x05, 115, 0x27, (byte) 0x88};
        TtnCayenneMessage msg = TtnCayenneMessage.parse(raw);

        assertEquals(1.0, msg.getPm10(), 0.01);
        assertEquals(0.25, msg.getPm2_5(), 0.01);
        assertEquals(-1.6, msg.getTempC(), 0.01);
        assertEquals(59.5, msg.getRhPerc(), 0.01);
        assertEquals(1012.0, msg.getPressureMillibar(), 0.1);
    }

}
