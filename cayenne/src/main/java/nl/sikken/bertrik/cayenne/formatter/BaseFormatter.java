package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;

/**
 * Abstract base class of formatters.
 */
public abstract class BaseFormatter implements IFormatter {

    /**
     * Gets an integer value from the byte buffer.
     * 
     * @param bb     the byte buffer
     * @param n      the number of bytes to get
     * @param signed whether it should be interpreted as signed value or not
     * @return the value
     */
    protected int getValue(ByteBuffer bb, int n, boolean signed) {
        int val = bb.get();
        val = signed ? val : val & 0xFF;
        for (int i = 1; i < n; i++) {
            val <<= 8;
            val += bb.get() & 0xFF;
        }
        return val;
    }

    /**
     * Puts an integer value into a byte buffer
     * 
     * @param bb    the byte buffer
     * @param n     the number of bytes to put
     * @param value the value to encode
     */
    protected void putValue(ByteBuffer bb, int n, int value) {
        int shift = (n - 1) * 8;
        for (int i = 0; i < n; i++) {
            byte b = (byte) ((value >> shift) & 0xFF);
            bb.put(b);
            shift -= 8;
        }
    }

}
