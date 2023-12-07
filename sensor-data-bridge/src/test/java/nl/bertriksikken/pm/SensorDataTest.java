package nl.bertriksikken.pm;

import org.junit.Assert;
import org.junit.Test;

public final class SensorDataTest {

    @Test
    public void testInRange() {
        SensorData sensorData = new SensorData();

        // value in range
        Assert.assertTrue(sensorData.addValue(ESensorItem.HUMIDITY, 100.0));
        Assert.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assert.assertEquals(100.0, sensorData.getValue(ESensorItem.HUMIDITY), 0.1);

        // value out of range
        Assert.assertFalse(sensorData.addValue(ESensorItem.PM10, -1.0));
        Assert.assertFalse(sensorData.hasValue(ESensorItem.PM10));

        // invalid value
        Assert.assertFalse(sensorData.addValue(ESensorItem.PM2_5, Double.NaN));
        Assert.assertFalse(sensorData.hasValue(ESensorItem.PM2_5));
    }

    @Test
    public void testString() {
        SensorData sensorData = new SensorData();
        String s = sensorData.toString();
        Assert.assertEquals("{}", s);
    }

}
