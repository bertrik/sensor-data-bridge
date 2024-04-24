package nl.bertriksikken.ttn.enddevice;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnConfig;
import nl.bertriksikken.ttn.dto.UplinkMessage;

public final class RunEndDeviceTest {

    private static final Logger LOG = LoggerFactory.getLogger(RunEndDeviceTest.class);

    public static void main(String[] args) throws IOException {
        RunEndDeviceTest test = new RunEndDeviceTest();
        test.testListEndDevices();
    }

    private void testListEndDevices() throws IOException {
        TtnConfig ttnConfig = new TtnConfig();
        TtnAppConfig appConfig = new TtnAppConfig();
        EndDeviceRegistry registry = EndDeviceRegistry.create(ttnConfig.getIdentityServerUrl(), Duration.ofSeconds(10),
                appConfig);
        List<EndDevice> endDevices = registry.listEndDevices();
        LOG.info("Found {} end devices", endDevices.size());

        // check last update time of first device
        String deviceId = endDevices.get(0).getDeviceId();

        EndDevice endDevice = registry.getNsEndDevice(deviceId, "mac_state.recent_uplinks");
        Instant lastUpdate = findLastUpdated(endDevice);
        LOG.info("Last update: {}", lastUpdate);
    }

    private Instant findLastUpdated(EndDevice endDevice) {
        Instant mostRecent = null;
        for (UplinkMessage uplink : endDevice.getMACState().getRecentUplinks()) {
            Instant instant = uplink.getReceivedAt();
            if ((mostRecent == null) || instant.isAfter(mostRecent)) {
                mostRecent = instant;
            }
        }
        return mostRecent;
    }

}
