package nl.bertriksikken.nbiot;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.nbiot.CdpMessage.Report;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public final class CdpMessageTest {

    @Test
    public void testDecodeJson() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/cdp_message.json")) {
            ObjectMapper mapper = new ObjectMapper();
            CdpMessage message = mapper.readValue(is, CdpMessage.class);

            Assertions.assertFalse(message.reports.isEmpty());
            Report report = message.reports.get(0);

            Assertions.assertEquals("IMEI:868333030676163", report.serialNumber);
            Assertions.assertEquals(1615660679246L, report.timestamp);
            Assertions.assertEquals("06f872fc-02b3-4b20-8394-3f19a7006ca9", report.subscriptionId);
            Assertions.assertEquals("uplinkMsg/0/data", report.resourcePath);
            Assertions.assertEquals("01a3021c", report.value);
        }
    }

    @Test
    public void testToString() {
        CdpMessage message = new CdpMessage();
        String s = message.toString();
        Assertions.assertNotNull(s);
    }
    
}
