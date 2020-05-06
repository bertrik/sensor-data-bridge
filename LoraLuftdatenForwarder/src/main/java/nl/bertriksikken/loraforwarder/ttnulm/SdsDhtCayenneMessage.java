package nl.bertriksikken.loraforwarder.ttnulm;

import java.util.Optional;

import nl.sikken.bertrik.cayenne.CayenneException;
import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;
import nl.sikken.bertrik.cayenne.ECayenneItem;

/**
 * Cayenne message containing SDS data (as analog values on channels 100 and 25) and DHT data.
 */
public final class SdsDhtCayenneMessage {

    private Optional<Double> pm10 = Optional.empty();
    private Optional<Double> pm2_5 = Optional.empty();;
    private Optional<Double> rhPerc = Optional.empty();;
    private Optional<Double> tempC = Optional.empty();;

    public SdsDhtCayenneMessage() {
    }

    public void parse(byte[] raw) throws PayloadParseException {
        CayenneMessage cayenneMessage = new CayenneMessage();
        try {
            cayenneMessage.parse(raw);
            
            CayenneItem p10 = cayenneMessage.ofChannel(1);
            if (p10 != null) {
                pm10 = Optional.of(p10.getValue().doubleValue());
            }
            CayenneItem p25 = cayenneMessage.ofChannel(2);
            if (p25 != null) {
                pm2_5 = Optional.of(p25.getValue().doubleValue());
            }
            CayenneItem temp = cayenneMessage.ofType(ECayenneItem.TEMPERATURE);
            if (temp != null) {
                tempC = Optional.of(temp.getValue().doubleValue());
            }
            CayenneItem rh = cayenneMessage.ofType(ECayenneItem.HUMIDITY);
            if (rh != null) {
                rhPerc = Optional.of(rh.getValue().doubleValue());
            }
        } catch (CayenneException e) {
            throw new PayloadParseException(e);
        }
    }

    public boolean hasPm10() {
        return pm10.isPresent();
    }
    
    public double getPm10() {
        return pm10.get();
    }

    public boolean hasPm2_5() {
        return pm2_5.isPresent();
    }
    
    public double getPm2_5() {
        return pm2_5.get();
    }

    public boolean hasRhPerc() {
        return rhPerc.isPresent();
    }
    
    public double getRhPerc() {
        return rhPerc.get();
    }

    public boolean hasTempC() {
        return tempC.isPresent();
    }
    public double getTempC() {
        return tempC.get();
    }
}
