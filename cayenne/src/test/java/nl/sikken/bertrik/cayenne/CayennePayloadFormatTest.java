package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * See https://community.mydevices.com/t/cayenne-lpp-2-0/7510
 */
public final class CayennePayloadFormatTest {

    @Test
    public void testPort() {
        assertNull(ECayennePayloadFormat.fromPort(0));
        assertEquals(ECayennePayloadFormat.DYNAMIC_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(1));
        assertEquals(ECayennePayloadFormat.PACKED_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(2));
    }

}
