package nl.bertriksikken.senscom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SensComPinTest {

    @Test
    public void testHappyFlow() {
        ESensComPin pin = ESensComPin.PARTICULATE_MATTER;
        assertEquals("1", pin.getPin());
    }

}
