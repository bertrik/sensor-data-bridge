package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;
import java.util.Locale;

public final class IntegerFormatter extends BaseFormatter {

    private final int length;
    private final int size;
    private final boolean signed;

    /**
     * Constructor.
     * 
     * @param length the number of elements
     * @param size   the size of each element
     * @param signed if the element is signed
     */
    public IntegerFormatter(int length, int size, boolean signed) {
        this.length = length;
        this.size = size;
        this.signed = signed;
    }

    @Override
    public Number[] parse(ByteBuffer bb) {
        Integer[] values = new Integer[length];
        for (int i = 0; i < length; i++) {
            values[i] = getValue(bb, size, signed);
        }
        return values;
    }

    @Override
    public String[] format(Number[] values) {
        String[] formatted = new String[length];
        for (int i = 0; i < length; i++) {
            formatted[i] = String.format(Locale.ROOT, "%d", values[i].intValue());
        }
        return formatted;
    }

    @Override
    public void encode(ByteBuffer bb, Number[] values) {
        for (int i = 0; i < length; i++) {
            putValue(bb, size, values[i].intValue());
        }
    }

}
