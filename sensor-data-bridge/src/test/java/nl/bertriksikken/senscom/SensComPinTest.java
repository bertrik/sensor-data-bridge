package nl.bertriksikken.senscom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SensComPinTest {

    @Test
    public void testHappyFlow() {
        ESensComPin pin = ESensComPin.PARTICULATE_MATTER;
        Assertions.assertEquals("1", pin.getPin());
    }

}
