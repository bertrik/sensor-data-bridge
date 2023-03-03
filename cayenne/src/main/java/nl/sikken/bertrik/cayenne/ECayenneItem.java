package nl.sikken.bertrik.cayenne;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import nl.sikken.bertrik.cayenne.formatter.FloatFormatter;
import nl.sikken.bertrik.cayenne.formatter.GpsFormatter;
import nl.sikken.bertrik.cayenne.formatter.IFormatter;
import nl.sikken.bertrik.cayenne.formatter.IntegerFormatter;

/**
 * Enumeration of possible Cayenne item types.
 */
public enum ECayenneItem {
    DIGITAL_INPUT(0, new IntegerFormatter(1, 1, false)), //
    DIGITAL_OUTPUT(1, new IntegerFormatter(1, 1, false)), //
    ANALOG_INPUT(2, new FloatFormatter(1, 2, 0.01, true)), //
    ANALOG_OUTPUT(3, new FloatFormatter(1, 2, 0.01, true)), //
    ILLUMINANCE(101, new FloatFormatter(1, 2, 1.0, false)), //
    PRESENCE(102, new IntegerFormatter(1, 1, false)), //
    TEMPERATURE(103, new FloatFormatter(1, 2, 0.1, true)), //
    HUMIDITY(104, new FloatFormatter(1, 1, 0.5, false)), //
    ACCELEROMETER(113, new FloatFormatter(3, 2, 0.001, true)), //
    BAROMETER(115, new FloatFormatter(1, 2, 0.1, false)), //
    GYROMETER(134, new FloatFormatter(3, 2, 0.01, true)), //
    GPS_LOCATION(136, new GpsFormatter()), //
    ;

    private final int type;
    private final IFormatter formatter;

    // reverse lookup table
    private static final Map<Integer, ECayenneItem> LOOKUP = new HashMap<Integer, ECayenneItem>();
    static {
        Stream.of(values()).forEach((e) -> LOOKUP.put(e.getType(), e));
    }

    /**
     * Constructor.
     * 
     * @param type      the data type
     * @param formatter the formatter for conversion to a string array
     */
    ECayenneItem(int type, IFormatter formatter) {
        this.type = type;
        this.formatter = formatter;
    }

    /**
     * Parses a type code into an enum.
     * 
     * @param type the type code
     * @return the enum, or null if not found
     * @throws CayenneException
     */
    public static ECayenneItem parse(int type) throws CayenneException {
        ECayenneItem item = LOOKUP.get(type);
        if (item == null) {
            throw new CayenneException("Invalid cayenne type " + type);
        }
        return item;
    }

    public int getType() {
        return type;
    }

    /**
     * Formats the measurement values into an array of strings.
     * 
     * @param values the values to encode
     * @return the formatted values
     */
    public String[] format(Number[] values) {
        return formatter.format(values);
    }

    /**
     * Parses the contents of the byte buffer into an array of numerical values.
     * 
     * @param bb the byte buffer to parse from
     * @return the numerical values
     */
    public Number[] parse(ByteBuffer bb) {
        return formatter.parse(bb);
    }

    /**
     * Encodes an array of numerical values into a byte buffer.
     * 
     * @param bb     the byte buffer to encode into
     * @param values the numerical values
     */
    public void encode(ByteBuffer bb, Number[] values) {
        formatter.encode(bb, values);
    }

}
