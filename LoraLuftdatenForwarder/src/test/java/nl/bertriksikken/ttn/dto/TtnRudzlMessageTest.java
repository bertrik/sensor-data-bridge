package nl.bertriksikken.ttn.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.loraforwarder.rudzl.dto.RudzlMessage;

public final class TtnRudzlMessageTest {
	
	@Test
	public void testDeserialize() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_rudzl_message.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);
	        Assert.assertEquals(4459, message.getCounter());
	        Map<String, Object> fields = message.getPayloadFields();
	        Assert.assertEquals(18789, fields.get("SDS_ID"));
	        Assert.assertEquals(0.4, (double) fields.get("PM10_Avg"), 0.01);
        }
	}

	@Test
	public void testDeserialize2() throws IOException {
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_rudzl_message_2.json")) {
	        ObjectMapper mapper = new ObjectMapper();
	        TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);
	        Assert.assertEquals(1439, message.getCounter());
	        Map<String, Object> fields = message.getPayloadFields();
	        Assert.assertEquals(18789, fields.get("SDS_ID"));
	        RudzlMessage rudzlMessage = new RudzlMessage(fields);
	        Assert.assertEquals(2.0, rudzlMessage.getPM10(), 0.01);
	        Assert.assertEquals(25.36, rudzlMessage.getT(), 0.01);
	        Assert.assertEquals(52.83, rudzlMessage.getRH(), 0.01);
	        Assert.assertEquals(1027.0, rudzlMessage.getP(), 0.1);
        }
	}

}
