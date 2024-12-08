package nl.bertriksikken.pm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SensorDataTest {

    @Test
    public void testInRange() {
        SensorData sensorData = new SensorData();

        // value in range
        sensorData.putValue(ESensorItem.HUMIDITY, 100.0);
        Assertions.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assertions.assertTrue(sensorData.hasValid(ESensorItem.HUMIDITY));
        Assertions.assertEquals(100.0, sensorData.getValue(ESensorItem.HUMIDITY), 0.1);

        // value out of range
        sensorData.putValue(ESensorItem.PM10, -1.0);
        Assertions.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assertions.assertFalse(sensorData.hasValid(ESensorItem.PM10));

        // invalid value
        sensorData.putValue(ESensorItem.PM2_5, Double.NaN);
        Assertions.assertTrue(sensorData.hasValue(ESensorItem.HUMIDITY));
        Assertions.assertFalse(sensorData.hasValid(ESensorItem.PM2_5));

        // null value
        sensorData.putValue(ESensorItem.PM1_0, null);
        Assertions.assertFalse(sensorData.hasValue(ESensorItem.PM1_0));
        Assertions.assertFalse(sensorData.hasValid(ESensorItem.PM1_0));
    }

    @Test
    public void testString() {
        SensorData sensorData = new SensorData();
        String s = sensorData.toString();
        Assertions.assertEquals("{}", s);

        // value in range
        sensorData.putValue(ESensorItem.PM2_5, 2.5);
        sensorData.putValue(ESensorItem.PM10, 10.0);
        sensorData.putValue(ESensorItem.HUMIDITY, 12.3);
        Assertions.assertEquals("{PM2_5=2.5ug/m3,PM10=10.0ug/m3,HUMIDITY=12.3%}", sensorData.toString());
    }

}
