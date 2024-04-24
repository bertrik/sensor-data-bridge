package nl.bertriksikken.ttn.dto;

import java.io.IOException;
import java.io.InputStream;

import nl.bertriksikken.ttn.dto.Ttnv3UplinkMessage;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.TtnUplinkMessage;

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
	        Ttnv3UplinkMessage ttnv3UplinkMessage = mapper.readValue(is, Ttnv3UplinkMessage.class);
	        TtnUplinkMessage message = ttnv3UplinkMessage.toTtnUplinkMessage(); 
	        
	        Assert.assertEquals("0000547AF1BF713C", message.getDevEui());
            Assert.assertEquals(19, message.getRawPayload().length);
            Assert.assertEquals(1, message.getPort());
            Assert.assertEquals(7, message.getSF());
        }
	}

}
