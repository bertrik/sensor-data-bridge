package nl.bertriksikken.ttn.rudzl.dto;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.loraforwarder.rudzl.dto.RudzlMessage;
import nl.bertriksikken.ttn.dto.TtnUplinkMessage;

public final class TtnRudzlMessageTest {
	
	@Test
	public void testDeserialize() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_rudzl_message.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);
	        Assert.assertEquals(4459, message.getCounter());
	        RudzlMessage rudzlMessage = new RudzlMessage(message.getPayloadFields());
	        Assert.assertEquals(18789, rudzlMessage.getSdsId());
	        Assert.assertEquals(0.4, rudzlMessage.getPM10(), 0.01);
        }
	}

	@Test
	public void testDeserialize2() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_rudzl_message_2.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);
	        Assert.assertEquals(1439, message.getCounter());
	        RudzlMessage rudzlMessage = new RudzlMessage(message.getPayloadFields());
	        Assert.assertEquals(18789, rudzlMessage.getSdsId());
	        Assert.assertEquals(2.0, rudzlMessage.getPM10(), 0.01);
	        Assert.assertEquals(25.36, rudzlMessage.getT(), 0.01);
	        Assert.assertEquals(52.83, rudzlMessage.getRH(), 0.01);
	        Assert.assertEquals(1027.0, rudzlMessage.getP(), 0.1);
        }
	}

}
