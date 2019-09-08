package nl.bertriksikken.ttn.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

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

}
