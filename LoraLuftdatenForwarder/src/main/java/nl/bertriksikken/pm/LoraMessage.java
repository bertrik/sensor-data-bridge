package nl.bertriksikken.pm;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.Optional;

public final class LoraMessage {

    private final double pm10;
    private final double pm2_5;
    private final Optional<Double> temp;
    private final Optional<Double> humidity;

    public LoraMessage(double pm10, double pm2_5, Optional<Double> temp, Optional<Double> humidity) {
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
        this.temp = temp;
        this.humidity = humidity;
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public Optional<Double> getTemp() {
        return temp;
    }

    public Optional<Double> getHumidity() {
        return humidity;
    }

    public static LoraMessage decode(byte[] data) throws ParseException {
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        try {
            int rawPm10 = bb.getShort() & 0xFFFF;
            double pm10 = rawPm10 / 10.0;
            int rawPm2_5 = bb.getShort() & 0xFFFF;
            double pm2_5 = rawPm2_5 / 10.0;
            int rawTemp = bb.getShort();
            Optional<Double> temp = getOptional(rawTemp, 10.0);
            int rawHumi = bb.getShort();
            Optional<Double> humi = getOptional(rawHumi, 10.0);
            return new LoraMessage(pm10, pm2_5, temp, humi);
        } catch (BufferUnderflowException e) {
            throw new ParseException("underflow", bb.position());
        }
    }

    private static Optional<Double> getOptional(int rawValue, double scale) {
        return ((rawValue & 0xFFFF) == 0xFFFF) ? Optional.empty() : Optional.of(rawValue / scale);
    }

}
