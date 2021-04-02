package nl.bertriksikken.loraforwarder.ttnulm;

import java.util.Arrays;
import java.util.Optional;

import nl.sikken.bertrik.cayenne.CayenneException;
import nl.sikken.bertrik.cayenne.CayenneItem;
import nl.sikken.bertrik.cayenne.CayenneMessage;
import nl.sikken.bertrik.cayenne.ECayenneItem;

/**
 * Cayenne message containing SDS data (as analog values on channels 0..4)
 * and DHT data.
 */
public final class TtnCayenneMessage {

    private static final int CHANNEL_PM2_5 = 2;
    private static final int CHANNEL_PM4 = 4;
    private static final int CHANNEL_PM10 = 1;
    private static final int CHANNEL_PM1 = 0;

    private Optional<Double> pm10 = Optional.empty();
    private Optional<Double> pm2_5 = Optional.empty();
    private Optional<Double> pm4 = Optional.empty();
    private Optional<Double> pm1 = Optional.empty();
    private Optional<Double> rhPerc = Optional.empty();
    private Optional<Double> tempC = Optional.empty();
    private Optional<Double> pressureMillibar = Optional.empty();
    private Optional<double[]> position = Optional.empty();

    public TtnCayenneMessage() {
    }

    public void parse(byte[] raw) throws PayloadParseException {
        CayenneMessage cayenneMessage = new CayenneMessage();
        try {
            cayenneMessage.parse(raw);

            CayenneItem p10 = cayenneMessage.find(ECayenneItem.ANALOG_INPUT, CHANNEL_PM10);
            if (p10 != null) {
                pm10 = Optional.of(p10.getValue().doubleValue());
            }
            CayenneItem p25 = cayenneMessage.find(ECayenneItem.ANALOG_INPUT, CHANNEL_PM2_5);
            if (p25 != null) {
                pm2_5 = Optional.of(p25.getValue().doubleValue());
            }
            CayenneItem p4 = cayenneMessage.find(ECayenneItem.ANALOG_INPUT, CHANNEL_PM4);
            if (p4 != null) {
                pm4 = Optional.of(p4.getValue().doubleValue());
            }
            CayenneItem p1 = cayenneMessage.find(ECayenneItem.ANALOG_INPUT, CHANNEL_PM1);
            if (p1 != null) {
                pm1 = Optional.of(p1.getValue().doubleValue());
            }
            CayenneItem temp = cayenneMessage.ofType(ECayenneItem.TEMPERATURE);
            if (temp != null) {
                tempC = Optional.of(temp.getValue().doubleValue());
            }
            CayenneItem rh = cayenneMessage.ofType(ECayenneItem.HUMIDITY);
            if (rh != null) {
                rhPerc = Optional.of(rh.getValue().doubleValue());
            }
            CayenneItem baro = cayenneMessage.ofType(ECayenneItem.BAROMETER);
            if (baro != null) {
                pressureMillibar = Optional.of(baro.getValue().doubleValue());
            }
            CayenneItem pos = cayenneMessage.ofType(ECayenneItem.GPS_LOCATION);
            if (pos != null) {
                position = Optional.of(Arrays.stream(pos.getValues()).mapToDouble(Number::doubleValue).toArray());

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

    public boolean hasPm4() {
        return pm4.isPresent();
    }

    public double getPm4() {
        return pm4.get();
    }

    public boolean hasPm2_5() {
        return pm2_5.isPresent();
    }

    public double getPm2_5() {
        return pm2_5.get();
    }

    public boolean hasPm1_0() {
        return pm1.isPresent();
    }

    public Double getPm1_0() {
        return pm1.get();
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

    public boolean hasPressureMillibar() {
        return pressureMillibar.isPresent();
    }

    public double getPressureMillibar() {
        return pressureMillibar.get();
    }

    public boolean hasPosition() {
        return position.isPresent();
    }

    public double[] getPosition() {
        return position.get();
    }

}
