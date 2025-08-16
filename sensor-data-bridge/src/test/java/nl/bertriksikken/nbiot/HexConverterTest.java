package nl.bertriksikken.nbiot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HexConverterTest {

    @Test
    public void testHappyFlow() {
        assertArrayEquals(new byte[0], HexConverter.fromString(""));
        assertArrayEquals(new byte[]{0}, HexConverter.fromString("00"));
        assertArrayEquals(new byte[]{1}, HexConverter.fromString("01"));
        assertArrayEquals(new byte[]{(byte) 0x80}, HexConverter.fromString("80"));
        assertArrayEquals(new byte[]{(byte) 0xAB}, HexConverter.fromString("ab"));
        assertArrayEquals(new byte[]{(byte) 0xAB}, HexConverter.fromString("AB"));
        assertArrayEquals(new byte[]{(byte) 0xFF}, HexConverter.fromString("fF"));

        assertArrayEquals(new byte[]{0, 0}, HexConverter.fromString("0000"));
        assertArrayEquals(new byte[]{0, 1}, HexConverter.fromString("0001"));
        assertArrayEquals(new byte[]{(byte) 0xAB, (byte) 0xCD}, HexConverter.fromString("ABCD"));
    }

    @Test
    public void testInvalidCharacters() {
        assertThrows(NumberFormatException.class, () ->
                HexConverter.fromString("xx"));
    }

    @Test
    public void testOddCharacters() {
        assertThrows(NumberFormatException.class, () -> HexConverter.fromString("000"));
    }

    @Test
    public void testToString() {
        assertEquals("(null)", HexConverter.toString(null));
        assertEquals("", HexConverter.toString(new byte[0]));
        assertEquals("123481", HexConverter.toString(new byte[]{0x12, 0x34, (byte) 0x81}));
    }

}
