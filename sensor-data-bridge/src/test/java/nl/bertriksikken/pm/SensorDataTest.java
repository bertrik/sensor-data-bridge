package nl.bertriksikken.pm;

import org.junit.Assert;
import org.junit.Test;

public final class SensorDataTest {

    @Test
    public void testInRange() {
        SensorData sensorData = new SensorData();

        // value in range
        sensorData.putValue(ESensorItem.HUMIDITY, 100.0);
        Assert.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assert.assertTrue(sensorData.hasValid(ESensorItem.HUMIDITY));
        Assert.assertEquals(100.0, sensorData.getValue(ESensorItem.HUMIDITY), 0.1);

        // value out of range
        sensorData.putValue(ESensorItem.PM10, -1.0);
        Assert.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assert.assertFalse(sensorData.hasValid(ESensorItem.PM10));

        // invalid value
        sensorData.putValue(ESensorItem.PM2_5, Double.NaN);
        Assert.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assert.assertFalse(sensorData.hasValid(ESensorItem.PM2_5));

        // null value
        sensorData.putValue(ESensorItem.PM1_0, null);
        Assert.assertFalse(sensorData.hasValue(ESensorItem.PM1_0));
        Assert.assertFalse(sensorData.hasValid(ESensorItem.PM1_0));
    }

    @Test
    public void testString() {
        SensorData sensorData = new SensorData();
        String s = sensorData.toString();
        Assert.assertEquals("{}", s);

        // value in range
        sensorData.putValue(ESensorItem.PM2_5, 2.5);
        sensorData.putValue(ESensorItem.PM10, 10.0);
        sensorData.putValue(ESensorItem.HUMIDITY, 12.3);
        Assert.assertEquals("{PM2_5=2.5ug/m3,PM10=10.0ug/m3,HUMIDITY=12.3%}", sensorData.toString());
    }

}
