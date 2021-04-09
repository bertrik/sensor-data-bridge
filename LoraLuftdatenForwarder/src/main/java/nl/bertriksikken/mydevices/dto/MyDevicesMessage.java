package nl.bertriksikken.mydevices.dto;

import java.util.ArrayList;

import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;

public final class MyDevicesMessage extends ArrayList<MyDevicesItem> {

    private static final long serialVersionUID = 1L;
    
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
