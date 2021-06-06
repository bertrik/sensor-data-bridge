package nl.bertriksikken.nbiot;

import java.util.Locale;

public final class HexConverter {
    
    private HexConverter() {
        // utility class has no public constructor
    }

    public static byte[] fromString(String hex) throws NumberFormatException {
        if ((hex.length() % 2) != 0) {
            throw new NumberFormatException("odd length string");
        }
        int len = hex.length() / 2;
        byte[] buf = new byte[len];
        for (int i = 0; i < len; i++) {
            buf[i] = (byte) (Integer.parseInt(hex.substring(2 * i, 2 * (i + 1)), 16) & 0xFF);
        }
        return buf;
    }
    
    public static String toString(byte[] data) {
        if (data == null) {
            return "(null)";
        }
        String hex = "";
        for (byte b : data) {
            hex += String.format(Locale.ROOT, "%02X", b);
        }
        return hex;
    }
    
}
