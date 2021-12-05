package nl.bertriksikken.soundkit;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

public final class NoiseMsg {

    // object mapper shared by all instances
    private static final ObjectMapper mapper = new ObjectMapper();
    private final NoiseJson noiseJson;

    NoiseMsg(NoiseJson jsonNode) {
        this.noiseJson = jsonNode;
    }

    public static NoiseMsg parse(String json) throws PayloadParseException {
        try {
            NoiseJson noise = mapper.readValue(json, NoiseJson.class);
            return new NoiseMsg(noise);
        } catch (IOException e) {
            throw new PayloadParseException(e);
        }
    }

    public void getSensorData(SensorData sensorData) {
        addValue(sensorData, ESensorItem.NOISE_LA_MIN, noiseJson.la.min);
        addValue(sensorData, ESensorItem.NOISE_LA_EQ, noiseJson.la.avg);
        addValue(sensorData, ESensorItem.NOISE_LA_MAX, noiseJson.la.max);
    }

    private void addValue(SensorData sensorData, ESensorItem item, double value) {
        if (Double.isFinite(value)) {
            sensorData.addValue(item, value);
        }
    }

}
