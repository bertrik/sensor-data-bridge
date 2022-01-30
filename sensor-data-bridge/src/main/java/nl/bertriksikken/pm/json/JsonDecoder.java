package nl.bertriksikken.pm.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

/**
 * Decodes a JSON payload, according to a configuration of JSON path and data items (assuming double).
 */
public final class JsonDecoder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonDecoderConfig config;

    public JsonDecoder(JsonDecoderConfig config) {
        this.config = new JsonDecoderConfig(config);
    }

    public void parse(String json, SensorData data) throws PayloadParseException {
        // parse JSON into generic structure
        JsonNode node;
        try {
            node = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new PayloadParseException(e);
        }
        // extract measurement items
        for (JsonDecoderItem item : config) {
            JsonNode field = node.at(item.path);
            double value = field.asDouble(Double.NaN);
            if (Double.isFinite(value)) {
                data.addValue(item.item, value);
            }
        }
    }

}
