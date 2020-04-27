package nl.sikken.bertrik.cayenne;

import org.junit.Assert;
import org.junit.Test;

/**
 * See https://community.mydevices.com/t/cayenne-lpp-2-0/7510
 */
public final class CayennePayloadFormatTest {

    @Test
    public void testPort() {
        Assert.assertNull(ECayennePayloadFormat.fromPort(0));
        Assert.assertEquals(ECayennePayloadFormat.DYNAMIC_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(1));
        Assert.assertEquals(ECayennePayloadFormat.PACKED_SENSOR_PAYLOAD, ECayennePayloadFormat.fromPort(2));
    }

}
