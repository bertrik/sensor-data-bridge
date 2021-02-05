package nl.bertriksikken.ttnv3.dto;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttnv3.dto.Ttnv3UplinkMessage.EndDeviceIds;
import nl.bertriksikken.ttnv3.dto.Ttnv3UplinkMessage.UplinkMessage;

/**
 * Unit tests related to TTN messages.
 */
public final class TtnV3UplinkMessageTest {

    /**
     * Reads an example message and verifies parsing.
     * 
     * @throws IOException
     */
    @Test
    public void testDecode() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/ttnv3_mqtt_message.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        Ttnv3UplinkMessage message = mapper.readValue(is, Ttnv3UplinkMessage.class);
	        
	        EndDeviceIds endDeviceIds = message.endDeviceIds;
	        Assert.assertNotNull(endDeviceIds.deviceId);
	        Assert.assertNotNull(endDeviceIds.deviceEui);
            Assert.assertNotNull(endDeviceIds.joinEui);
            Assert.assertNotNull(endDeviceIds.deviceAddress);
	        
            Assert.assertNotNull(message.receivedAt);
            
            UplinkMessage uplinkMessage = message.uplinkMessage;
            Assert.assertNotEquals(0, uplinkMessage.fport);
            Assert.assertNotEquals(0, uplinkMessage.fcnt);
            Assert.assertNotNull(uplinkMessage.payload);
        }
	}

}
