package nl.bertriksikken.gls;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeoLocationRequest {

    @JsonProperty("considerIp")
    private final boolean considerIp;

    @JsonProperty("wifiAccessPoints")
    private final List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>();

    public GeoLocationRequest(boolean considerIp) {
        this.considerIp = considerIp;
    }

    /**
     * @param mac            the 6-byte mac address of the access point
     * @param signalStrength signal strength in dBm
     * @param channel        the WiFi channel
     */
    public void add(byte[] mac, int signalStrength, int channel) {
        List<String> bytes = new ArrayList<>();
        for (byte b : mac) {
            bytes.add(String.format(Locale.ROOT, "%02X", b));
        }
        String macAddress = String.join(":", bytes);
        add(macAddress, signalStrength, channel);
    }

    public void add(String macAddress, int signalStrength, int channel) {
        wifiAccessPoints.add(new WifiAccessPoint(macAddress, signalStrength, channel));
    }

    private static final class WifiAccessPoint {
        @JsonProperty("macAddress")
        private final String macAddress;

        @JsonProperty("signalStrength")
        private final int signalStrength;

        @JsonProperty("channel")
        private final int channel;

        WifiAccessPoint(String macAddress, int signalStrength, int channel) {
            this.macAddress = macAddress;
            this.signalStrength = signalStrength;
            this.channel = channel;
        }
    }
}
