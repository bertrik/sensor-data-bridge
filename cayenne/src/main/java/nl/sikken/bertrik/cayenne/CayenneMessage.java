package nl.sikken.bertrik.cayenne;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A cayenne message containing cayenne data items.
 */
public final class CayenneMessage {
    
    private final List<CayenneItem> items = new ArrayList<>();
    
    /**
     * Parses the byte array into a cayenne message.
     * 
     * @param data the raw data
     * @return the cayenne message
     * @throws CayenneException in case of a parsing problem
     */
    public static CayenneMessage parse(byte[] data) throws CayenneException {
        CayenneMessage message = new CayenneMessage();
        ByteBuffer bb = ByteBuffer.wrap(data);
        while (bb.hasRemaining()) {
            CayenneItem item = CayenneItem.parse(bb);
            message.add(item);
        }
        return message;
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
     * @throws CayenneException in case something went wrong during encoding (e.g. message too big)
     */
    public byte[] encode(int maxSize) throws CayenneException {
        ByteBuffer bb = ByteBuffer.allocate(maxSize).order(ByteOrder.LITTLE_ENDIAN);
        for (CayenneItem item : items) {
        	item.encode(bb);
        }
        return Arrays.copyOfRange(bb.array(), 0, bb.position());
    }
    
    /**
     * @return an immutable list of measurement items in the order it appears in the raw data
     */
    public List<CayenneItem> getItems() {
        return Collections.unmodifiableList(items);
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
