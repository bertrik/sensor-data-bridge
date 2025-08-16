package nl.bertriksikken.nbiot;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bertriksikken.nbiot.CdpMessage.Report;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class CdpMessageTest {

    @Test
    public void testDecodeJson() throws IOException {
        // decode JSON
        try (InputStream is = this.getClass().getResourceAsStream("/cdp_message.json")) {
            ObjectMapper mapper = new ObjectMapper();
            CdpMessage message = mapper.readValue(is, CdpMessage.class);

            assertFalse(message.reports.isEmpty());
            Report report = message.reports.get(0);

            assertEquals("IMEI:868333030676163", report.serialNumber);
            assertEquals(1615660679246L, report.timestamp);
            assertEquals("06f872fc-02b3-4b20-8394-3f19a7006ca9", report.subscriptionId);
            assertEquals("uplinkMsg/0/data", report.resourcePath);
            assertEquals("01a3021c", report.value);
        }
    }

    @Test
    public void testToString() {
        CdpMessage message = new CdpMessage();
        String s = message.toString();
        assertNotNull(s);
    }
    
}
