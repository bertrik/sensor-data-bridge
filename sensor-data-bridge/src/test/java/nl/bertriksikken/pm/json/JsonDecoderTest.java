package nl.bertriksikken.pm.json;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.POJONode;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

public final class JsonDecoderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDecode() throws PayloadParseException, IOException {
        URL url = this.getClass().getResource("/decoded_fields.json");
        String json = mapper.readTree(url).toPrettyString();

        JsonDecoder decoder = new JsonDecoder();
        SensorData sensorData = new SensorData();

        JsonDecoderConfig config = new JsonDecoderConfig();
        config.add(new JsonDecoderItem("/la/avg", ESensorItem.NOISE_LA_EQ));
        JsonNode configNode = new POJONode(config);
        decoder.parse(configNode, json, sensorData);

        Assert.assertEquals(35.5, sensorData.getValue(ESensorItem.NOISE_LA_EQ), 0.1);
    }

    @Test
    public void testDecodeJsonPayload() throws IOException {
        String json = mapper.readTree(getClass().getResource("/json_payload.json")).toPrettyString();

        JsonDecoder decoder = new JsonDecoder();
        JsonDecoderConfig config = new JsonDecoderConfig();
        config.add(new JsonDecoderItem("/airpressure", ESensorItem.PRESSURE, 100.0));
        config.add(new JsonDecoderItem("/temperature", ESensorItem.TEMPERATURE));
        JsonNode configNode = new POJONode(config);

        SensorData sensorData = new SensorData();
        decoder.parse(configNode, json, sensorData);

        Assert.assertEquals(99700, sensorData.getValue(ESensorItem.PRESSURE), 0.1);
        Assert.assertEquals(10.1, sensorData.getValue(ESensorItem.TEMPERATURE), 0.1);
    }

}
