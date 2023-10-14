package nl.bertriksikken.senscom;

import org.junit.Assert;
import org.junit.Test;

public final class SensComPinTest {

    @Test
    public void testHappyFlow() {
        ESensComPin pin = ESensComPin.PARTICULATE_MATTER;
        Assert.assertEquals("1", pin.getPin());
    }

}
