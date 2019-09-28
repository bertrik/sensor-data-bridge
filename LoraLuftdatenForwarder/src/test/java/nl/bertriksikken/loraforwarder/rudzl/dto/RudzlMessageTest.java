package nl.bertriksikken.loraforwarder.rudzl.dto;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public final class RudzlMessageTest {

	@Test
	public void testDecodeInteger() {
		Map<String, Object> map = new HashMap<>();
		map.put("PM10_Avg", 2);
		map.put("SDS_ID", 12345);
		
		RudzlMessage msg = new RudzlMessage(map);
		Assert.assertEquals(2.0, msg.getPM10(), 0.01);
        Assert.assertEquals(12345, msg.getSdsId());
	}
	
}
