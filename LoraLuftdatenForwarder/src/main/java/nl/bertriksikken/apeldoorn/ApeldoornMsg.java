package nl.bertriksikken.apeldoorn;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

/**
 * A message as encoded in the Apeldoorn project. Processes the "decoded fields" JSON.
 */
public final class ApeldoornMsg {
    
    // object mapper shared by all instances
    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonNode jsonNode;
    
    ApeldoornMsg(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public static ApeldoornMsg parse(String json) throws PayloadParseException {
        try {
            JsonNode node = mapper.readTree(json);
            return new ApeldoornMsg(node);
        } catch (IOException e) {
            throw new PayloadParseException(e);
        }
    }
    
    public void getSensorData(SensorData sensorData) {
        getDouble(sensorData, jsonNode.at("/pm10"), ESensorItem.PM10);
        getDouble(sensorData, jsonNode.at("/pm2p5"), ESensorItem.PM2_5);
        getDouble(sensorData, jsonNode.at("/rh"), ESensorItem.HUMI);
        getDouble(sensorData, jsonNode.at("/temp"), ESensorItem.TEMP);
    }

    private boolean getDouble(SensorData sensorData, JsonNode node, ESensorItem item) {
        double value = node.asDouble(Double.NaN);
        if (Double.isFinite(value)) {
            sensorData.addValue(item, value);
            return true;
        }
        return false;
    }
    
    
}
