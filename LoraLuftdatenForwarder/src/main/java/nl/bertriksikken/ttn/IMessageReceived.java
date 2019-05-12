package nl.bertriksikken.ttn;

import java.time.Instant;

/**
 * Interface of the callback from the MQTT listener.
 */
public interface IMessageReceived {

    /**
     * Indicates that a message was received.
     * 
     * @param instant time stamp of message reception
     * @param topic   the topic
     * @param message the message
     */
    void messageReceived(Instant instant, String topic, String message) throws Exception;

}
