package nl.bertriksikken.loraforwarder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Base configuration class.
 */
public abstract class BaseConfig {

    private final Map<String, String> values = new LinkedHashMap<>();
    private final Map<String, String> comments = new LinkedHashMap<>();

    /**
     * Adds a property.
     * 
     * @param key     the key
     * @param value   the default value
     * @param comment the comment
     */
    protected void add(String key, String value, String comment) {
        values.put(key, value);
        comments.put(key, comment);
    }

    /**
     * Returns the value associated with the key.
     * 
     * @param key the key
     * @return the value
     */
    protected String get(String key) {
        return values.get(key);
    }

    /**
     * Load settings from stream.
     * 
     * @param is input stream containing the settings
     * @throws IOException in case of an IO problem
     */
    public final void load(InputStream is) throws IOException {
        final Properties properties = new Properties();
        properties.load(is);
        for (String e : values.keySet()) {
            String value = properties.getProperty(e);
            if (value != null) {
                values.put(e, value);
            }
        }
    }

    /**
     * Save settings to stream.
     * 
     * @param os the output stream
     * @throws IOException in case of an IO problem
     */
    public final void save(OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, StandardCharsets.US_ASCII)) {
            for (String e : values.keySet()) {
                // comment line
                writer.append("# " + comments.get(e) + "\n");
                writer.append(e + "=" + get(e) + "\n");
                writer.append("\n");
            }
        }
    }

}
