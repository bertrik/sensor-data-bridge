package nl.bertriksikken.loraforwarder;

import nl.bertriksikken.gls.GeoLocationRequest;
import nl.bertriksikken.gls.GeoLocationResponse;
import nl.bertriksikken.gls.GeoLocationService;
import nl.bertriksikken.loraforwarder.util.CatchingRunnable;
import nl.bertriksikken.ttn.TtnUplinkMessage;
import nl.bertriksikken.ttn.enddevice.EndDevice;
import nl.bertriksikken.ttn.enddevice.EndDeviceRegistry;
import nl.bertriksikken.ttn.enddevice.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles commands and response from LoRaWAN devices.
 */
public final class CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    public static final int LORAWAN_PORT = 100;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final GeoLocationService geoLocationService;
    private final EndDeviceRegistry endDeviceRegistry;

    CommandHandler(GeoLocationService geoLocationService, EndDeviceRegistry endDeviceRegistry) {
        this.geoLocationService = geoLocationService;
        this.endDeviceRegistry = endDeviceRegistry;
    }

    public void start() {
        // nothing to do here
    }

    public void stop() {
        executor.shutdownNow();
    }

    /**
     * Test message on port 100: 00 9C 1C 12 F6 EB C0 D3 01 9C 1C 12 F6 F5 42 C9 01
     */
    void processResponse(TtnUplinkMessage uplink) {
        ByteBuffer bb = ByteBuffer.wrap(uplink.getRawPayload()).order(ByteOrder.BIG_ENDIAN);

        int cmd = bb.get() & 0xFF;
        if (cmd == 0) {
            executor.execute(new CatchingRunnable(LOG, () -> handleWifiLocalisation(bb, uplink.getDevId())));
        } else {
            LOG.warn("Unhandled command {}", cmd);
        }
    }

    void handleWifiLocalisation(ByteBuffer bb, String devId) {
        try {
            GeoLocationRequest request = new GeoLocationRequest(false);
            while (bb.hasRemaining()) {
                byte[] mac = new byte[6];
                bb.get(mac);
                int rssi = bb.get();
                int channel = bb.get() & 0xFF;
                request.add(mac, rssi, channel);
            }
            GeoLocationResponse response;
            response = geoLocationService.geoLocate(request);
            if (Double.isFinite(response.getAccuracy())) {
                EndDevice endDevice = endDeviceRegistry.buildEndDevice(devId);
                Location location = new Location(response.getLatitude(), response.getLongitude());
                endDevice.setLocation(EndDevice.LOCATION_USER, location);
                endDeviceRegistry.updateEndDevice(endDevice, List.of("locations"));
            }
        } catch (IOException e) {
            LOG.warn("Caught IOException", e);
        }
    }

}
