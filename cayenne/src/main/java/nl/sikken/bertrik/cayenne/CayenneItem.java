package nl.sikken.bertrik.cayenne;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

/**
 * Representation of one measurement item in a cayenne message.
 */
public final class CayenneItem {
    
    private final int channel;
    private final ECayenneItem type;
    private final Number[] values;

    /**
     * Constructor.
     * 
     * @param channel the unique channel
     * @param type the type
     * @param values the values
     */
    public CayenneItem(int channel, ECayenneItem type, Number[] values) {
        this.channel = channel;
        this.type = type;
        this.values = values.clone();
    }

    /**
     * Constructor for a single value
     * 
     * @param channel the unique channel
     * @param type the type
     * @param value the value
     */
    public CayenneItem(int channel, ECayenneItem type, Number value) {
        this(channel, type, new Number[] {value});
    }

    public int getChannel() {
        return channel;
    }

    public ECayenneItem getType() {
        return type;
    }

    public Number[] getValues() {
        return values.clone();
    }
    
    public Number getValue() {
        return values[0];
    }

    public String[] format() {
        return type.format(values);
    }
    
    /**
     * Parses one item from the byte buffer and returns it.
     * 
     * @param bb the byte buffer
     * @return a new cayenne item
     * @throws CayenneException if an error occurs during parsing
     */
    public static CayenneItem parse(ByteBuffer bb) throws CayenneException {
        try {
            int channel = bb.get();
            int type = bb.get() & 0xFF;
            ECayenneItem ct = ECayenneItem.parse(type);
            Number[] values = ct.parse(bb);
            return new CayenneItem(channel, ct, values);
        } catch (BufferUnderflowException e) {
            throw new CayenneException(e);
        }
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{chan=%d,type=%s,value=%s}", channel, type, Arrays.toString(format()));  
    }

    public void encode(ByteBuffer bb) throws CayenneException {
        try {
            bb.put((byte)channel);
            bb.put((byte)type.getType());
            type.encode(bb, values);
        } catch (BufferOverflowException e) {
            throw new CayenneException(e);
        }
        
    }
    
}
