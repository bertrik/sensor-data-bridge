package nl.bertriksikken.pm.sps30;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import nl.bertriksikken.pm.PayloadParseException;

/**
 * Payload message with SPS30 data (mass, count, particle size)
 */
public final class Sps30Message {

    public static final int LORAWAN_PORT = 30;

    private double pm05;
    private double pm1_0;
    private double pm2_5;
    private double pm4_0;
    private double pm10;
    private double n0_5;
    private double n1_0;
    private double n2_5;
    private double n4_0;
    private double n10;
    private double ps;

    public static Sps30Message parse(byte[] raw) throws PayloadParseException {
        Sps30Message message = new Sps30Message();
        try {
            ByteBuffer bb = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN);
            message.pm1_0 = (bb.getShort() & 0xFFFF) / 10.0;
            message.pm2_5 = (bb.getShort() & 0xFFFF) / 10.0;
            message.pm4_0 = (bb.getShort() & 0xFFFF) / 10.0;
            message.pm10 = (bb.getShort() & 0xFFFF) / 10.0;
            message.n0_5 = bb.getShort() & 0xFFFF;
            message.n1_0 = bb.getShort() & 0xFFFF;
            message.n2_5 = bb.getShort() & 0xFFFF;
            message.n4_0 = bb.getShort() & 0xFFFF;
            message.n10 = bb.getShort() & 0xFFFF;
            message.ps = bb.getShort() & 0xFFFF;
        } catch (BufferUnderflowException e) {
            throw new PayloadParseException(e);
        }
        return message;
    }

    public double getPm05() {
        return pm05;
    }

    public double getPm1_0() {
        return pm1_0;
    }

    public double getPm2_5() {
        return pm2_5;
    }

    public double getPm4_0() {
        return pm4_0;
    }

    public double getPm10() {
        return pm10;
    }

    public double getN0_5() {
        return n0_5;
    }

    public double getN1_0() {
        return n1_0;
    }

    public double getN2_5() {
        return n2_5;
    }

    public double getN4_0() {
        return n4_0;
    }

    public double getN10() {
        return n10;
    }

    public double getPs() {
        return ps;
    }
}
