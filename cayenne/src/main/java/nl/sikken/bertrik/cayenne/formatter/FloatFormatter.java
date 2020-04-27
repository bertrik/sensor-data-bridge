package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Formatter for cayenne items which represent real numbers.
 */
public final class FloatFormatter extends BaseFormatter {

    private final int length;
    private final int size;
    private final double scale;
    private final boolean signed;
    private final String formatString;

    /**
     * Constructor.
     * 
     * @param length number of elements in the return vector (e.g. 3 for a gyro reading)
     * @param size the number of bytes in each element
     * @param scale scale factor to apply, this determines the number of significant digits when formatting
     * @param signed whether the number should be interpreted as signed or unsigned
     */
    public FloatFormatter(int length, int size, double scale, boolean signed) {
        this.length = length;
        this.size = size;
        this.scale = scale;
        this.signed = signed;
        this.formatString = createFormatString(scale);
    }
    
    private String createFormatString(double scale) {
        int decimals = 0;
        for (double d = scale; d < 1.0; d *= 10) {
            decimals++;
        }
        return String.format(Locale.ROOT, "%%.%df", decimals);
    }
    
    @Override
    public Double[] parse(ByteBuffer bb) {
        Double[] values = new Double[length];
        for (int i = 0; i < length; i++) {
            values[i] = scale * getValue(bb, size, signed);
        }
        return values;
    }
    
    @Override
    public String[] format(Number[] values) {
        String[] formatted = new String[length];
        for (int i = 0; i < length; i++) {
            formatted[i] = String.format(Locale.ROOT, formatString, values[i].doubleValue());
        }
        return formatted;
    }

    @Override
    public void encode(ByteBuffer bb, Number[] values) {
        for (int i = 0; i < length; i++) {
            putValue(bb, size, (int)Math.round(values[i].doubleValue() / scale));
        }
    }

}
