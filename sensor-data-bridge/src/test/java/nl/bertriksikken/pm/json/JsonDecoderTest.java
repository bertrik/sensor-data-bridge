package nl.bertriksikken.pm.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.POJONode;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

public final class JsonDecoderTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDecode() throws IOException {
        URL url = this.getClass().getResource("/decoded_fields.json");
        String json = mapper.readTree(url).toPrettyString();

        JsonDecoder decoder = new JsonDecoder();
        SensorData sensorData = new SensorData();

        JsonDecoderConfig config = new JsonDecoderConfig();
        config.add(new JsonDecoderItem("/la/avg", ESensorItem.NOISE_LA_EQ));
        JsonNode configNode = new POJONode(config);
        decoder.parse(configNode, json, sensorData);

        Assertions.assertEquals(35.5, sensorData.getValue(ESensorItem.NOISE_LA_EQ), 0.1);
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

        Assertions.assertEquals(99700, sensorData.getValue(ESensorItem.PRESSURE), 0.1);
        Assertions.assertEquals(10.1, sensorData.getValue(ESensorItem.TEMPERATURE), 0.1);
    }

    @Test
    public void testInvalidHumidity() throws JsonProcessingException {
        JsonDecoderConfig config = new JsonDecoderConfig();
        config.add(new JsonDecoderItem("/rh", ESensorItem.HUMIDITY));
        JsonNode configNode = new POJONode(config);

        JsonDecoder decoder = new JsonDecoder();
        SensorData sensorData = new SensorData();
        String json = "{\"rh\":-1}";

        decoder.parse(configNode, json, sensorData);
        Assertions.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assertions.assertFalse(ESensorItem.HUMIDITY.inRange(sensorData.getValue(ESensorItem.HUMIDITY)));
    }

}
