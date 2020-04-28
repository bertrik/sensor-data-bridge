package nl.bertriksikken.loraforwarder.ttnulm;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TtnUlmMessage {

    private double pm10;
    private double pm2_5;
    private double rhPerc;
    private double tempC;

    public TtnUlmMessage() {
    }
    
    public void parse(byte[] raw)  throws PayloadParseException {
        try {
            ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN);
            this.pm10 = bb.getShort() / 100.0;
            this.pm2_5 = bb.getShort() / 100.0;
            this.rhPerc = bb.getShort() / 100.0;
            this.tempC = bb.getShort() / 100.0;
        } catch (BufferUnderflowException e) {
            throw new PayloadParseException(e);
        }
    }

    public double getPm10() {
        return pm10;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public double getRhPerc() {
        return rhPerc;
    }

    public double getTempC() {
        return tempC;
    }        
    
}
