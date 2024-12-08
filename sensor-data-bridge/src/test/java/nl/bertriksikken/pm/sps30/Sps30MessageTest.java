package nl.bertriksikken.pm.sps30;

import nl.bertriksikken.pm.PayloadParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class Sps30MessageTest {

    @Test
    public void testEmptyData() {
        Assertions.assertThrows(PayloadParseException.class, () ->
                Sps30Message.parse(new byte[]{}));
    }

    @Test
    public void testZeroData() throws PayloadParseException {
        Sps30Message message = Sps30Message.parse(new byte[20]);
        Assertions.assertNotNull(message);
    }
}
