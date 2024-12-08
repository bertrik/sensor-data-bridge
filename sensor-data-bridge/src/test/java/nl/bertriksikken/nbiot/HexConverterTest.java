package nl.bertriksikken.nbiot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class HexConverterTest {

    @Test
    public void testHappyFlow() {
        Assertions.assertArrayEquals(new byte[0], HexConverter.fromString(""));
        Assertions.assertArrayEquals(new byte[]{0}, HexConverter.fromString("00"));
        Assertions.assertArrayEquals(new byte[]{1}, HexConverter.fromString("01"));
        Assertions.assertArrayEquals(new byte[]{(byte) 0x80}, HexConverter.fromString("80"));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xAB}, HexConverter.fromString("ab"));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xAB}, HexConverter.fromString("AB"));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xFF}, HexConverter.fromString("fF"));

        Assertions.assertArrayEquals(new byte[]{0, 0}, HexConverter.fromString("0000"));
        Assertions.assertArrayEquals(new byte[]{0, 1}, HexConverter.fromString("0001"));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xAB, (byte) 0xCD}, HexConverter.fromString("ABCD"));
    }

    @Test
    public void testInvalidCharacters() {
        Assertions.assertThrows(NumberFormatException.class, () ->
                HexConverter.fromString("xx"));
    }

    @Test
    public void testOddCharacters() {
        Assertions.assertThrows(NumberFormatException.class, () -> HexConverter.fromString("000"));
    }

    @Test
    public void testToString() {
        Assertions.assertEquals("(null)", HexConverter.toString(null));
        Assertions.assertEquals("", HexConverter.toString(new byte[0]));
        Assertions.assertEquals("123481", HexConverter.toString(new byte[]{0x12, 0x34, (byte) 0x81}));
    }

}
