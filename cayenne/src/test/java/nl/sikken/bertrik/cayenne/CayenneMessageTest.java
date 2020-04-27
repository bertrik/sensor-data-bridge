package nl.sikken.bertrik.cayenne;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests for CayenneMessage.
 */
public final class CayenneMessageTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(CayenneMessageTest.class);
    
    private final int MAX_BUF_SIZE = 500;
    
    /**
     * Verifies example from specification.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testTwoTemperatureSensors() throws CayenneException {
        final byte[] data = {0x03, 0x67, 0x01, 0x10, 0x05, 0x67, 0x00, (byte) 0xFF};
        final CayenneMessage payload = CayenneMessage.parse(data);
        LOG.info("payload: {}", payload);
        
        Assert.assertArrayEquals(new String[] {"27.2"}, payload.ofChannel(3).format());
        Assert.assertArrayEquals(new String[] {"25.5"}, payload.ofChannel(5).format());
    }
    
    /**
     * Verifies example from specification.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testTemperaturePlusAccel() throws CayenneException {
        final byte[] data = 
            {0x01, 0x67, (byte) 0xFF, (byte) 0xD7, 0x06, 0x71, 0x04, (byte) 0xD2, (byte) 0xFB, 0x2E, 0x00, 0x00};
        final CayenneMessage payload = CayenneMessage.parse(data);

        Assert.assertArrayEquals(new String[] {"-4.1"}, payload.ofChannel(1).format());
        Assert.assertArrayEquals(new String[] {"1.234", "-1.234", "0.000"}, payload.ofChannel(6).format());
    }

    /**
     * Verifies example from specification.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testGps() throws CayenneException {
        final byte[] data = 
            {0x01, (byte) 0x88, 0x06, 0x076, 0x5f, (byte) 0xf2, (byte) 0x96, 0x0a, 0x00, 0x03, (byte) 0xe8};
        final CayenneMessage payload = CayenneMessage.parse(data);

        Assert.assertArrayEquals(new String[] {"42.3519", "-87.9094", "10.00"}, payload.ofChannel(1).format());
    }

    /**
     * Verifies parsing of humidity value.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testHumidity() throws CayenneException {
        final byte[] data = {1, 104, 100};
        final CayenneMessage payload = CayenneMessage.parse(data);
        
        Assert.assertArrayEquals(new String[] {"50.0"}, payload.ofChannel(1).format());
    }
    
    /**
     * Verifies parsing of some actual data from a sodaq one.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testActualData() throws CayenneException {
        final String base64 = "AYgH8CEAt1D//zgCAmDQA2cBDg==";
        final byte[] data = Base64.getDecoder().decode(base64);
        final CayenneMessage payload = CayenneMessage.parse(data);

        Assert.assertArrayEquals(new String[] {"52.0225", "4.6928", "-2.00"}, payload.ofChannel(1).format());
        Assert.assertArrayEquals(new String[] {"247.84"}, payload.ofChannel(2).format());
        Assert.assertArrayEquals(new String[] {"27.0"}, payload.ofChannel(3).format());
    }
    
    /**
     * Verifies parsing of some actual data from a sodaq one, with a fix applied to the voltage value.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testActualData2() throws CayenneException {
    	final String base64 = "AYgH8CEAt03/+VwCAgGfA2cA8A==";
        final byte[] data = Base64.getDecoder().decode(base64);
        final CayenneMessage payload = CayenneMessage.parse(data);

        // verify we can get at the data by channel
        Assert.assertArrayEquals(new String[] {"52.0225", "4.6925", "-17.00"}, payload.ofChannel(1).format());
        Assert.assertArrayEquals(new String[] {"4.15"}, payload.ofChannel(2).format());
        Assert.assertArrayEquals(new String[] {"24.0"}, payload.ofChannel(3).format());

        // verify we can also get data by type
        Assert.assertArrayEquals(new String[] {"52.0225", "4.6925", "-17.00"}, 
                                 payload.ofType(ECayenneItem.GPS_LOCATION).format());
        Assert.assertArrayEquals(new String[] {"4.15"}, payload.ofType(ECayenneItem.ANALOG_INPUT).format());
        Assert.assertArrayEquals(new String[] {"24.0"}, payload.ofType(ECayenneItem.TEMPERATURE).format());
        
        // verify non-existing channel and type
        Assert.assertNull(payload.ofChannel(0));
        Assert.assertNull(payload.ofType(ECayenneItem.BAROMETER));
        
        // verify toString method
        Assert.assertNotNull(payload.toString());
    }

    /**
     * Verifies parsing an empty string.
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testParseEmpty() throws CayenneException {
        final CayenneMessage payload = CayenneMessage.parse(new byte[0]);
        Assert.assertTrue(payload.getItems().isEmpty());
    }
 
    /**
     * Verifies parsing a short buffer
     * @throws CayenneException in case of a parsing exception
     */
    @Test(expected = CayenneException.class)
    public void testShortBuffer() throws CayenneException {
        CayenneMessage.parse(new byte[] {0});
    }

    /**
     * Verifies parsing of a buffer containing a non-supported data type.
     * @throws CayenneException in case of a parsing exception
     */
    @Test(expected = CayenneException.class)
    public void testInvalidType() throws CayenneException {
        CayenneMessage.parse(new byte[] {0, 100});
    }
    
    /**
     * Verifies parsing of a buffer containing insufficient data during parsing.
     * @throws CayenneException in case of a parsing exception
     */
    @Test(expected = CayenneException.class)
    public void testShortData() throws CayenneException {
        CayenneMessage.parse(new byte[] {2, 1});
    }
    
    /**
     * Verifies encoding of a float value.
     * 
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void encodeFloat() throws CayenneException {
        final CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(1, ECayenneItem.ANALOG_INPUT, -12.34));
        
        final byte[] encoded = message.encode(MAX_BUF_SIZE);
        
        final CayenneMessage decoded = CayenneMessage.parse(encoded);
        Assert.assertEquals(-12.34, decoded.getItems().get(0).getValues()[0].doubleValue(), 0.01);
    }
    
    /**
     * Verifies encoding of a humidity value.
     * 
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void encodeHumidity() throws CayenneException {
        final CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(1, ECayenneItem.HUMIDITY, 35.5));
        
        final byte[] encoded = message.encode(MAX_BUF_SIZE);
        final CayenneMessage decoded = CayenneMessage.parse(encoded);

        final CayenneItem item = decoded.getItems().get(0);
        Assert.assertEquals(ECayenneItem.HUMIDITY, item.getType());
        Assert.assertEquals(35.5, item.getValues()[0].doubleValue(), 0.1);
        Assert.assertEquals("35.5", item.format()[0]);
    }
    
    /**
     * Verifies encoding of a digital input.
     * 
     * @throws CayenneException in case of a parsing exception
     */
    @Test
    public void testDigitalInput() throws CayenneException {
        final CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(1, ECayenneItem.DIGITAL_INPUT, 1));
        
        final byte[] encoded = message.encode(MAX_BUF_SIZE);
        final CayenneMessage decoded = CayenneMessage.parse(encoded);

        final CayenneItem item = decoded.getItems().get(0);
        Assert.assertEquals(ECayenneItem.DIGITAL_INPUT, item.getType());
        Assert.assertEquals(1, item.getValues()[0].intValue());
    }
    
    /**
     * Verifies encoding/decoding of a presence value (e.g. number of satellites)
     * 
     * @throws CayenneException
     */
    @Test
    public void testPresence() throws CayenneException {
        final CayenneMessage message = new CayenneMessage();
        message.add(new CayenneItem(1, ECayenneItem.PRESENCE, 7));
        
        final byte[] encoded = message.encode(MAX_BUF_SIZE);
        final CayenneMessage decoded = CayenneMessage.parse(encoded);

        final CayenneItem item = decoded.getItems().get(0);
        Assert.assertEquals(ECayenneItem.PRESENCE, item.getType());
        Assert.assertEquals(7, item.getValues()[0].intValue());
        Assert.assertEquals("7", item.format()[0]);
    }
    
}

