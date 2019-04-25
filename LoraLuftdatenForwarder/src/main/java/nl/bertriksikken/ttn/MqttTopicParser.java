package nl.bertriksikken.ttn;

/**
 * Very basic topic parser.
 */
public final class MqttTopicParser {

    private final String topic;

    /**
     * Constructor.
     * 
     * @param topic the topic string
     */
    public MqttTopicParser(String topic) {
        this.topic = topic;
    }

    /**
     * @return the last element of the MQTT topic
     */
    public String getLast() {
        String[] parts = topic.split("/");
        int last = parts.length - 1;
        return (last >= 0) ? parts[last] : "";
    }

}
