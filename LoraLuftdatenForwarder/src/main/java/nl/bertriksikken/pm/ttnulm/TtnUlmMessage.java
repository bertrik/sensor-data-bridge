package nl.bertriksikken.pm.ttnulm;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import nl.bertriksikken.pm.PayloadParseException;

public final class TtnUlmMessage {

    private double pm10;
    private double pm2_5;
    private double rhPerc;
    private double tempC;

    public static TtnUlmMessage parse(byte[] raw)  throws PayloadParseException {
        TtnUlmMessage message = new TtnUlmMessage();
        try {
            ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN);
            message.pm10 = bb.getShort() / 100.0;
            message.pm2_5 = bb.getShort() / 100.0;
            message.rhPerc = bb.getShort() / 100.0;
            message.tempC = bb.getShort() / 100.0;
        } catch (BufferUnderflowException e) {
            throw new PayloadParseException(e);
        }
        return message;
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
