package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;

/**
 * Interface for cayenne data structures that can be formatted as an array of strings. 
 */
public interface IFormatter {

    /**
     * Parses raw data into an array of doubles.
     * 
     * @param bb the byte buffer
     * @return the parsed value as a number
     */
    Number[] parse(ByteBuffer bb);

    /** 
     * Formats the data into an array of strings.
     * For example, for a GPS location it outputs: latitude in [0], longitude in [1], altitude in [2].
     * 
     * @param values the value as number array
     * @return the string representation
     */
    String[] format(Number[] values);

    /**
     * Encodes the data into the byte buffer
     * 
     * @param bb the buffer to encode to
     * @param values the values to encode
     */
    void encode(ByteBuffer bb, Number[] values);

}
