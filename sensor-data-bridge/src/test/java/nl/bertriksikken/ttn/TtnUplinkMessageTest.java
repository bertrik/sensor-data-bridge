package nl.bertriksikken.ttn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TtnUplinkMessageTest {

    @Test
    public void testString() {
        TtnUplinkMessage message = new TtnUplinkMessage("app", "device", "eui", new byte[] { 1, 2, 3 }, "{}", 1);
        Assertions.assertNotNull(message.toString());
        System.out.println(message);
    }

}
