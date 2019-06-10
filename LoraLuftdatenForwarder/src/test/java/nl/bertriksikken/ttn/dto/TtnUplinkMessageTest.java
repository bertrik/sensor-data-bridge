package nl.bertriksikken.ttn.dto;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests related to TTN messages.
 */
public final class TtnUplinkMessageTest {

    /**
     * Reads an example message and verifies parsing.
     * 
     * @throws IOException
     */
    @Test
    public void testDecode() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_mqtt_message.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);
	        Assert.assertNotNull(message);
	        Assert.assertNotNull(message.appId);
	        Assert.assertNotNull(message.devId);
	        Assert.assertNotNull(message.hardwareSerial);
	        Assert.assertNotNull(message.port);
	        Assert.assertNotNull(message.counter);
	        Assert.assertNotNull(message.rawPayload);
        }
	}

}
