package nl.bertriksikken.pm.sps30;

import nl.bertriksikken.pm.PayloadParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class Sps30MessageTest {

    @Test
    public void testEmptyData() {
        assertThrows(PayloadParseException.class, () ->
                Sps30Message.parse(new byte[]{}));
    }

    @Test
    public void testZeroData() throws PayloadParseException {
        Sps30Message message = Sps30Message.parse(new byte[20]);
        assertNotNull(message);
    }
}
