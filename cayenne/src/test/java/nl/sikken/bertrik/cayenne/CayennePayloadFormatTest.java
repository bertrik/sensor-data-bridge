package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * See https://community.mydevices.com/t/cayenne-lpp-2-0/7510
 */
public final class CayennePayloadFormatTest {

    @Test
    public void testPort() {
        Assertions.assertNull(ECayennePayloadFormat.fromPort(0));
        Assertions.assertEquals(ECayennePayloadFormat.DYNAMIC_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(1));
        Assertions.assertEquals(ECayennePayloadFormat.PACKED_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(2));
    }

}
