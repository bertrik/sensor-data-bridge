package nl.bertriksikken.apeldoorn;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;

public final class ApeldoornMsgTest {

    /**
     * Verifies that a valid JSON can be parsed.
     */
    @Test
    public void testValidDecode() throws PayloadParseException {
        String json = "{\"pm10\": 17.5,\n"
                + "\"pm2p5\": 10.5,\n"
                + "\"rh\": 99.9,\n"
                + "\"temp\": 12.6}";
        SensorData sensorData = new SensorData();
        ApeldoornMsg msg = ApeldoornMsg.parse(json);
        msg.getSensorData(sensorData);
        
        Assert.assertEquals(17.5, sensorData.getValue(ESensorItem.PM10), 0.1);
        Assert.assertEquals(10.5, sensorData.getValue(ESensorItem.PM2_5), 0.1);
        Assert.assertEquals(99.9, sensorData.getValue(ESensorItem.HUMI), 0.1);
        Assert.assertEquals(12.6, sensorData.getValue(ESensorItem.TEMP), 0.1);
    }

    /**
     * Verifies that an empty structure does not lead to an exception.
     */
    @Test
    public void testEmptyJson() throws PayloadParseException {
        SensorData sensorData = new SensorData();
        ApeldoornMsg msg1 = ApeldoornMsg.parse("");
        msg1.getSensorData(sensorData);
        ApeldoornMsg msg2 = ApeldoornMsg.parse("{}");
        msg2.getSensorData(sensorData);
    }

}
