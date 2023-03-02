package nl.bertriksikken.pm.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.pm.SensorData;

/**
 * Decodes a JSON payload, according to a configuration of JSON path and data
 * items (assuming double).
 */
public final class JsonDecoder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void parse(JsonNode config, String json, SensorData data) throws JsonProcessingException {
        // convert JsonNode into JsonDecoderConfig object
        JsonDecoderConfig jsonDecoderConfig = OBJECT_MAPPER.treeToValue(config, JsonDecoderConfig.class);

        // parse JSON into generic structure
        JsonNode node = OBJECT_MAPPER.readTree(json);

        // extract measurement items
        for (JsonDecoderItem item : jsonDecoderConfig) {
            JsonNode field = node.at(item.path);
            double value = field.asDouble(Double.NaN);
            if (Double.isFinite(value)) {
                data.addValue(item.item, value);
            }
        }
    }

}
