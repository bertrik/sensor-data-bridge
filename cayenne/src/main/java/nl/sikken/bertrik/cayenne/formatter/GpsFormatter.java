package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * Formatter for cayenne items which represent a GPS position.
 */
public final class GpsFormatter extends BaseFormatter {

    private static final double LAT_LON_SCALE = 1E-4;
    private static final double ALT_SCALE = 1E-2;

    @Override
    public Double[] parse(ByteBuffer bb) {
        double lat = LAT_LON_SCALE * getValue(bb, 3, true);
        double lon = LAT_LON_SCALE * getValue(bb, 3, true);
        double alt = ALT_SCALE * getValue(bb, 3, true);
        return new Double[] { lat, lon, alt };
    }

    @Override
    public String[] format(Number[] values) {
        return new String[] { String.format(Locale.ROOT, "%.4f", values[0]),
                String.format(Locale.ROOT, "%.4f", values[1]), String.format(Locale.ROOT, "%.2f", values[2]) };
    }

    @Override
    public void encode(ByteBuffer bb, Number[] values) {
        putValue(bb, 3, (int) Math.round(values[0].doubleValue() / LAT_LON_SCALE));
        putValue(bb, 3, (int) Math.round(values[1].doubleValue() / LAT_LON_SCALE));
        putValue(bb, 3, (int) Math.round(values[2].doubleValue() / ALT_SCALE));
    }

}
