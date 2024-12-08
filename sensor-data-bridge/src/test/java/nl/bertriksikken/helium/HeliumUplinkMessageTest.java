package nl.bertriksikken.helium;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.helium.HeliumUplinkMessage.HotSpot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class HeliumUplinkMessageTest {

    @Test
    public void testDecode() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/helium_uplink.json")) {
            ObjectMapper mapper = new ObjectMapper();
            HeliumUplinkMessage uplink = mapper.readValue(is, HeliumUplinkMessage.class);
            Assertions.assertNotNull(uplink);

            Assertions.assertEquals("6081F9D16837130E", uplink.appEui);
            Assertions.assertEquals("5A010048", uplink.devAddr);
            Assertions.assertEquals("0004A30B001F21FA", uplink.devEui);
            Assertions.assertEquals(0, uplink.fcnt);
            Assertions.assertEquals("kissmapper", uplink.name);
            Assertions.assertEquals(1, uplink.port);
            Assertions.assertEquals(1631457565832L, uplink.reportedAt);
            Assertions.assertArrayEquals(new byte[]{3}, uplink.payload);

            HotSpot hotSpot = uplink.hotSpots.get(0);
            Assertions.assertEquals(52.01745, hotSpot.latitude, 0.00001);
            Assertions.assertEquals(4.729876, hotSpot.longitude, 0.00001);
            Assertions.assertEquals("melted-quartz-antelope", hotSpot.name);
            Assertions.assertEquals(-120, hotSpot.rssi, 0.1);
            Assertions.assertEquals(-7.5, hotSpot.snr, 0.1);
        }

    }

}
