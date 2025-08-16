package nl.sikken.bertrik.cayenne;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for SimpleCayenne.
 */
public final class SimpleCayenneTest {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleCayenneTest.class);

    /**
     * Verifies basic functionality by adding some items and encoding it into a
     * message
     *
     * @throws CayenneException in case of a problem encoding/decoding
     */
    @Test
    public void testEncode() throws CayenneException {
        SimpleCayenne cayenne = new SimpleCayenne();
        cayenne.addAccelerometer(1, 1.0, 2.0, 3.0);
        cayenne.addAnalogInput(2, 3.82);
        cayenne.addAnalogOutput(3, 3.15);
        cayenne.addBarometricPressure(4, 100000);
        cayenne.addDigitalInput(5, 55);
        cayenne.addDigitalOutput(6, 66);
        cayenne.addGps(7, 52.0, 4.0, -1.0);
        cayenne.addGyrometer(8, 1.0, 2.0, 3.0);
        cayenne.addIlluminance(9, 1000.0);
        cayenne.addPresence(10, 42);
        cayenne.addRelativeHumidity(11, 50.0);
        cayenne.addTemperature(12, 19.0);
        LOG.info("Encoded message: {}", cayenne);

        // encode it
        byte[] data = cayenne.encode(500);
        assertNotNull(data);

        // decode it
        CayenneMessage message = new CayenneMessage();
        message.parse(data);
        assertEquals(12, message.getItems().size());
        assertEquals(3.82, message.ofType(ECayenneItem.ANALOG_INPUT).getValue().doubleValue(), 0.01);
        assertEquals(55, message.ofType(ECayenneItem.DIGITAL_INPUT).getValue().intValue());
        assertEquals(66, message.ofType(ECayenneItem.DIGITAL_OUTPUT).getValue().intValue());
        assertEquals(52.0, message.ofType(ECayenneItem.GPS_LOCATION).getValues()[0].doubleValue(), 0.1);
        assertEquals(42, message.ofType(ECayenneItem.PRESENCE).getValue().intValue(), 42);
        assertEquals(19.0, message.ofType(ECayenneItem.TEMPERATURE).getValue().doubleValue(), 0.1);
    }

    /**
     * Verifies that a simple cayenne message with non-unique channels is rejected.
     *
     * @throws CayenneException in case of a problem encoding/decoding
     */
    @Test
    public void testNonUniqueChannel() throws CayenneException {
        SimpleCayenne cayenne = new SimpleCayenne();
        cayenne.addTemperature(1, 19.0);
        assertThrows(CayenneException.class, () ->
                cayenne.addAnalogInput(1, 3.90));
    }

}
