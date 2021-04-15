package nl.bertriksikken.ttn;

import java.util.Arrays;
import java.util.Locale;

/**
 * Common class containing only the fields from the TTN upload message relevant
 * to us.
 */
public final class TtnUplinkMessage {

    private final String deviceEui;
    private final byte[] rawPayload;
    private final int port;
    private final int sf;

    public TtnUplinkMessage(String deviceEui, byte[] rawPayload, int port, int sf) {
        this.deviceEui = deviceEui;
        this.rawPayload = rawPayload.clone();
        this.port = port;
        this.sf = sf;
    }

    public String getDeviceEui() {
        return deviceEui;
    }

    public byte[] getRawPayload() {
        return rawPayload.clone();
    }

    public int getPort() {
        return port;
    }

    public int getSF() {
        return sf;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "EUI %s, data %s, port %d, SF %d", deviceEui, Arrays.toString(rawPayload),
                port, sf);
    }

}
