package nl.bertriksikken.nbiot;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.nbiot.CdpMessage.Report;

public final class CdpMessageTest {

    @Test
    public void testDecodeJson() throws JsonParseException, JsonMappingException, IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/cdp_message.json")) {
            ObjectMapper mapper = new ObjectMapper();
            CdpMessage message = mapper.readValue(is, CdpMessage.class);

            Assert.assertFalse(message.reports.isEmpty());
            Report report = message.reports.get(0);

            Assert.assertEquals("IMEI:868333030676163", report.serialNumber);
            Assert.assertEquals(1615660679246L, report.timestamp);
            Assert.assertEquals("06f872fc-02b3-4b20-8394-3f19a7006ca9", report.subscriptionId);
            Assert.assertEquals("uplinkMsg/0/data", report.resourcePath);
            Assert.assertEquals("01a3021c", report.value);
        }
    }

    @Test
    public void testToString() {
        CdpMessage message = new CdpMessage();
        String s = message.toString();
        Assert.assertNotNull(s);
    }
    
}
