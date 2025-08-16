package nl.bertriksikken.helium;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.helium.HeliumUplinkMessage.HotSpot;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HeliumUplinkMessageTest {

    @Test
    public void testDecode() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/helium_uplink.json")) {
            ObjectMapper mapper = new ObjectMapper();
            HeliumUplinkMessage uplink = mapper.readValue(is, HeliumUplinkMessage.class);
            assertNotNull(uplink);

            assertEquals("6081F9D16837130E", uplink.appEui());
            assertEquals("5A010048", uplink.devAddr());
            assertEquals("0004A30B001F21FA", uplink.devEui());
            assertEquals(0, uplink.fcnt());
            assertEquals("kissmapper", uplink.name());
            assertEquals(1, uplink.port());
            assertEquals(1631457565832L, uplink.reportedAt());
            assertArrayEquals(new byte[]{3}, uplink.payload());

            HotSpot hotSpot = uplink.hotSpots().get(0);
            assertEquals(52.01745, hotSpot.latitude(), 0.00001);
            assertEquals(4.729876, hotSpot.longitude(), 0.00001);
            assertEquals("melted-quartz-antelope", hotSpot.name());
            assertEquals(-120, hotSpot.rssi(), 0.1);
            assertEquals(-7.5, hotSpot.snr(), 0.1);
        }

    }

}
