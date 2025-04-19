package nl.sikken.bertrik.cayenne;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A cayenne message containing cayenne data items.
 */
public final class CayenneMessage {

    private final ECayennePayloadFormat format;
    private final List<CayenneItem> items = new ArrayList<>();

    public CayenneMessage() {
        this(ECayennePayloadFormat.DYNAMIC_SENSOR_PAYLOAD);
    }

    /**
     * @param format the payload format (e.g. parsed from the LoRaWAN port),
     *               currently only DYNAMIC_SENSOR_PAYLOAD and PACKED_SENSOR_PAYLOAD
     *               are supported.
     */
    public CayenneMessage(ECayennePayloadFormat format) {
        this.format = switch (format) {
            case DYNAMIC_SENSOR_PAYLOAD -> format;
            case PACKED_SENSOR_PAYLOAD -> format;
            default -> throw new IllegalArgumentException("Payload format not supported: " + format);
        };
    }

    /**
     * Parses the byte array into a cayenne message.
     *
     * @param data the raw data
     * @throws CayenneException in case of a parsing problem
     */
    @SuppressWarnings("StatementSwitchToExpressionSwitch")
    public void parse(byte[] data) throws CayenneException {
        CayenneItem item;
        ByteBuffer bb = ByteBuffer.wrap(data);
        int channel = 0;
        while (bb.hasRemaining()) {
            switch (format) {
                case DYNAMIC_SENSOR_PAYLOAD:
                    item = CayenneItem.parse(bb);
                    break;
                case PACKED_SENSOR_PAYLOAD:
                    item = CayenneItem.parsePacked(bb, channel);
                    channel++;
                    break;
                default:
                    throw new IllegalStateException("Unsupported cayenne payload: " + format);
            }
            add(item);
        }
    }

    /**
     * Adds a cayenne measurement item to the message.
     *
     * @param item the item to add
     */
    public void add(CayenneItem item) {
        items.add(item);
    }

    /**
     * Encodes the cayenne message into a byte array.
     *
     * @param maxSize the maximum size of the cayenne message
     * @return the byte array.
     * @throws CayenneException in case something went wrong during encoding (e.g.
     *                          message too big)
     */
    public byte[] encode(int maxSize) throws CayenneException {
        ByteBuffer bb = ByteBuffer.allocate(maxSize).order(ByteOrder.LITTLE_ENDIAN);
        for (CayenneItem item : items) {
            item.encode(bb);
        }
        return Arrays.copyOfRange(bb.array(), 0, bb.position());
    }

    /**
     * Returns a list of measurement items in the order it appears in the raw data
     */
    public List<CayenneItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * Finds an item by type and channel.
     *
     * @param type    the desired type
     * @param channel the desired channel
     * @return the item, or null if it does not exist
     */
    public CayenneItem find(ECayenneItem type, int channel) {
        return items.stream().filter(i -> (i.getType() == type) && (i.getChannel() == channel)).findFirst().orElse(null);
    }

    /**
     * Finds an item by type.
     *
     * @param type the desired type
     * @return the item, or null if it does not exist
     */
    public CayenneItem ofType(ECayenneItem type) {
        return items.stream().filter(i -> (i.getType() == type)).findFirst().orElse(null);
    }

    /**
     * Finds an item by channel.
     *
     * @param channel the desired channel
     * @return the item, or null if it does not exist
     */
    public CayenneItem ofChannel(int channel) {
        return items.stream().filter(i -> (i.getChannel() == channel)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return Arrays.toString(items.toArray());
    }

}
