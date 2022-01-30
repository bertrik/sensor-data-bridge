package nl.bertriksikken.pm.json;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

public final class JsonDecoderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDecode() throws PayloadParseException, IOException {
        URL url = this.getClass().getResource("/decoded_fields.json");
        String json = mapper.readTree(url).toPrettyString();

        JsonDecoderConfig config = new JsonDecoderConfig();
        JsonDecoder decoder = new JsonDecoder(config);
        SensorData sensorData = new SensorData();
        decoder.parse(json, sensorData);

        Assert.assertEquals(46.0, sensorData.getValue(ESensorItem.NOISE_LA_EQ), 0.1);
    }

}
