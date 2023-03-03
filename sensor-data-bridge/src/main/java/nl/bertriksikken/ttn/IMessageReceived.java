package nl.bertriksikken.ttn;

/**
 * Interface of the callback from the MQTT listener.
 */
public interface IMessageReceived {

    /**
     * Indicates that a message was received.
     * 
     * @param uplink the uplink message
     */
    void messageReceived(TtnUplinkMessage uplink) throws Exception;

}
