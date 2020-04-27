package nl.bertriksikken.pm;

import java.util.Locale;
import java.util.Optional;

/**
 * Representation of a message received from the MQTT stream.
 */
public final class SensorMessage {

    private SensorSds sds;
    private SensorBme bme;

    /**
     * Constructor.
     */
    public SensorMessage(SensorSds sds) {
        this.sds = sds;
    }

    public SensorSds getSds() {
        return sds;
    }

    public void setBme(SensorBme bme) {
    	this.bme = bme;
    }
    
    public Optional<SensorBme> getBme() {
        return Optional.ofNullable(bme);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "{sds=%s,bme=%s}", sds, bme);
    }

}
