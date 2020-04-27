package nl.bertriksikken.loraforwarder.ttnulm;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.ttn.dto.TtnUplinkMessage;

public final class TtnUlmMessageTest {

    @Test
    public void testDeserialize() throws IOException, PayloadParseException {
        try (InputStream is = this.getClass().getResourceAsStream("/ttn_ttnulm_message.json")) {
            ObjectMapper mapper = new ObjectMapper();
            TtnUplinkMessage message = mapper.readValue(is, TtnUplinkMessage.class);

            TtnUlmMessage ulmMessage = new TtnUlmMessage();
            ulmMessage.parse(message.getRawPayload());
            
            Assert.assertEquals(3.0, ulmMessage.getPm10(), 0.01);
            Assert.assertEquals(3.0, ulmMessage.getPm2_5(), 0.01);
            Assert.assertEquals(39.5, ulmMessage.getRhPerc(), 0.01);
            Assert.assertEquals(21.6, ulmMessage.getTempC(), 0.01);
        }
    }

}
