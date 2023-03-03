package nl.sikken.bertrik.cayenne;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper around CayenneMessage to make composing a Cayenne message a bit
 * easier.
 * 
 * This makes it similar to the C API shown on
 * https://mydevices.com/cayenne/docs/lora/#lora-cayenne-low-power-payload
 */
public final class SimpleCayenne {

    private final CayenneMessage message = new CayenneMessage();
    private final Set<Integer> channels = new HashSet<>();

    public void addDigitalInput(int channel, int value) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.DIGITAL_INPUT, value);
        addItem(item);
    }

    public void addDigitalOutput(int channel, int value) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.DIGITAL_OUTPUT, value);
        addItem(item);
    }

    public void addAnalogInput(int channel, double value) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.ANALOG_INPUT, value);
        addItem(item);
    }

    public void addAnalogOutput(int channel, double value) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.ANALOG_OUTPUT, value);
        addItem(item);
    }

    public void addIlluminance(int channel, double lux) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.ILLUMINANCE, lux);
        addItem(item);
    }

    public void addPresence(int channel, int value) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.PRESENCE, value);
        addItem(item);
    }

    public void addTemperature(int channel, double temperature) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.TEMPERATURE, temperature);
        addItem(item);
    }

    public void addRelativeHumidity(int channel, double humidity) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.HUMIDITY, humidity);
        addItem(item);
    }

    public void addAccelerometer(int channel, double x, double y, double z) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.ACCELEROMETER, new Double[] { x, y, z });
        addItem(item);
    }

    public void addBarometricPressure(int channel, double hpa) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.BAROMETER, hpa);
        addItem(item);
    }

    public void addGyrometer(int channel, double x, double y, double z) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.GYROMETER, new Double[] { x, y, z });
        addItem(item);
    }

    public void addGps(int channel, double latitude, double longitude, double altitude) throws CayenneException {
        CayenneItem item = new CayenneItem(channel, ECayenneItem.GPS_LOCATION,
                new Double[] { latitude, longitude, altitude });
        addItem(item);
    }

    private void addItem(CayenneItem item) throws CayenneException {
        // verify that channel is unique
        int channel = item.getChannel();
        if (channels.contains(channel)) {
            throw new CayenneException("Channel id " + channel + " need to be unique!");
        }
        // add the item
        channels.add(channel);
        message.add(item);
    }

    /**
     * Encodes the data into the supplied buffer.
     * 
     * @param maxSize maximum size of the message
     * @return the length of data encoded
     * @throws CayenneException in case something went wrong during encoding (e.g.
     *                          message too big)
     */
    public byte[] encode(int maxSize) throws CayenneException {
        return message.encode(maxSize);
    }

    @Override
    public String toString() {
        return message.toString();
    }

}
