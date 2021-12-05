package nl.bertriksikken.helium;

import java.io.IOException;
import java.io.InputStream;

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

            Assert.assertEquals("6081F9D16837130E", uplink.appEui);
            Assert.assertEquals("5A010048", uplink.devAddr);
            Assert.assertEquals("0004A30B001F21FA", uplink.devEui);
            Assert.assertEquals(0, uplink.fcnt);
            Assert.assertEquals("kissmapper", uplink.name);
            Assert.assertEquals(1, uplink.port);
            Assert.assertEquals(1631457565832L, uplink.reportedAt);
            Assert.assertArrayEquals(new byte[] { 3 }, uplink.payload);

            HotSpot hotSpot = uplink.hotSpots.get(0);
            Assert.assertEquals(52.01745, hotSpot.latitude, 0.00001);
            Assert.assertEquals(4.729876, hotSpot.longitude, 0.00001);
            Assert.assertEquals("melted-quartz-antelope", hotSpot.name);
            Assert.assertEquals(-120, hotSpot.rssi, 0.1);
            Assert.assertEquals(-7.5, hotSpot.snr, 0.1);
        }

    }

}
