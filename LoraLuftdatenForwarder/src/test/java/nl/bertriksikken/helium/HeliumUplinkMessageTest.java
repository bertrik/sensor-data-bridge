package nl.bertriksikken.helium;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.helium.HeliumUplinkMessage.HotSpot;

public class HeliumUplinkMessageTest {

    @Test
    public void testDecode() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/helium_uplink.json")) {
            ObjectMapper mapper = new ObjectMapper();
            HeliumUplinkMessage uplink = mapper.readValue(is, HeliumUplinkMessage.class);
            Assert.assertNotNull(uplink);

            Assert.assertEquals("3655731B237B9BA8", uplink.appEui);
            Assert.assertEquals("13040048", uplink.devAddr);
            Assert.assertEquals("ED9196B2424BF383", uplink.devEui);
            Assert.assertEquals(87, uplink.fcnt);
            Assert.assertEquals("Home Disco DevKit", uplink.name);
            Assert.assertEquals(1, uplink.port);
            Assert.assertEquals(1606869315, uplink.reportedAt);
            Assert.assertArrayEquals(new byte[] { 1, -120, 5, -56, -119, -19, 95, -78, 0, 30, -36, 2, 103, 1, -12, 3,
                    113, 3, -24, 7, -48, 11, -72 }, uplink.payload);

            HotSpot hotSpot = uplink.hotSpots.get(0);
            Assert.assertEquals(37.9003, hotSpot.latitude, 0.0001);
            Assert.assertEquals(-122.0720, hotSpot.longitude, 0.0001);
            Assert.assertEquals("square-goldenrod-gorilla", hotSpot.name);
            Assert.assertEquals(-31, hotSpot.rssi, 0.1);
            Assert.assertEquals(12.2, hotSpot.snr, 0.1);
        }

    }

}
