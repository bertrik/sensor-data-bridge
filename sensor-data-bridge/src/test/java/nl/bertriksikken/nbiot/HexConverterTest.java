package nl.bertriksikken.nbiot;

import org.junit.Assert;
import org.junit.Test;

public final class HexConverterTest {

    @Test
    public void testHappyFlow() {
        Assert.assertArrayEquals(new byte[0], HexConverter.fromString(""));
        Assert.assertArrayEquals(new byte[] {0}, HexConverter.fromString("00"));
        Assert.assertArrayEquals(new byte[] {1}, HexConverter.fromString("01"));
        Assert.assertArrayEquals(new byte[] {(byte) 0x80}, HexConverter.fromString("80"));
        Assert.assertArrayEquals(new byte[] {(byte) 0xAB}, HexConverter.fromString("ab"));
        Assert.assertArrayEquals(new byte[] {(byte) 0xAB}, HexConverter.fromString("AB"));
        Assert.assertArrayEquals(new byte[] {(byte) 0xFF}, HexConverter.fromString("fF"));

        Assert.assertArrayEquals(new byte[] {0, 0}, HexConverter.fromString("0000"));
        Assert.assertArrayEquals(new byte[] {0, 1}, HexConverter.fromString("0001"));
        Assert.assertArrayEquals(new byte[] {(byte) 0xAB, (byte) 0xCD}, HexConverter.fromString("ABCD"));
    }
    
    @Test(expected = NumberFormatException.class)
    public void testInvalidCharacters() {
        HexConverter.fromString("xx");
    }

    @Test(expected = NumberFormatException.class)
    public void testOddCharacters() {
        Assert.assertArrayEquals(new byte[] {0}, HexConverter.fromString("000"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("(null)", HexConverter.toString(null));
        Assert.assertEquals("", HexConverter.toString(new byte[0]));
        Assert.assertEquals("123481", HexConverter.toString(new byte[] {0x12, 0x34, (byte)0x81}));
    }
    
}
