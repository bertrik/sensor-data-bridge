package nl.bertriksikken.mydevices.dto;

import java.util.ArrayList;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;

public final class MyDevicesMessage extends ArrayList<MyDevicesItem> {

    private static final long serialVersionUID = 1L;
    
    public static MyDevicesMessage fromSensorData(SensorData data) {
        MyDevicesMessage message = new MyDevicesMessage();
        if (data.hasValue(ESensorItem.PM10)) {
            message.add(new MyDevicesItem(1, "analog_sensor", data.getValue(ESensorItem.PM10), "null"));
        }
        if (data.hasValue(ESensorItem.PM2_5)) {
            message.add(new MyDevicesItem(2, "analog_sensor", data.getValue(ESensorItem.PM2_5), "null"));
        }
        if (data.hasValue(ESensorItem.PM4_0)) {
            message.add(new MyDevicesItem(4, "analog_sensor", data.getValue(ESensorItem.PM4_0), "null"));
        }
        if (data.hasValue(ESensorItem.PM1_0)) {
            message.add(new MyDevicesItem(0, "analog_sensor", data.getValue(ESensorItem.PM1_0), "null"));
        }
        if (data.hasValue(ESensorItem.HUMI)) {
            message.add(new MyDevicesItem(10, "rel_hum", data.getValue(ESensorItem.HUMI), "p"));
        }
        if (data.hasValue(ESensorItem.TEMP)) {
            message.add(new MyDevicesItem(11, "temp", data.getValue(ESensorItem.TEMP), "c"));
        }
        if (data.hasValue(ESensorItem.PRESSURE)) {
            message.add(new MyDevicesItem(12, "bp", data.getValue(ESensorItem.PRESSURE), "pa"));
        }
        return message;
    }
    
    public static MyDevicesMessage fromCayenne(CayenneMessage cayenneMessage) {
        MyDevicesMessage message = new MyDevicesMessage();
        for (CayenneItem item : cayenneMessage.getItems()) {
            int channel = item.getChannel();
            Double value = item.getValue().doubleValue();
            switch (item.getType()) {
            case ANALOG_INPUT:
                message.add(new MyDevicesItem(channel, "analog_sensor", value, "null"));
                break;
            case TEMPERATURE:
                message.add(new MyDevicesItem(channel, "temp", value, "c"));
                break;
            case HUMIDITY:
                message.add(new MyDevicesItem(channel, "rel_hum", value, "p"));
                break;
            case BAROMETER:
                message.add(new MyDevicesItem(channel, "bp", value, "hpa"));
                break;
            default:
                // ignore
                break;
            }
        }
        return message;
    }

}
