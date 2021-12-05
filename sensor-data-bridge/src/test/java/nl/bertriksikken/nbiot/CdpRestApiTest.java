package nl.bertriksikken.nbiot;

import org.junit.Test;

import nl.bertriksikken.nbiot.CdpMessage.Report;

public final class CdpRestApiTest {

    /**
     * Basic happy flow test of ping().
     */
    @Test
    public void testPing() {
        CdpRestApi api = new CdpRestApi();
        api.ping();
    }

    /**
     * Basic happy flow test of uplink().
     */
    @Test
    public void testUplink() {
        CdpMessage cdpMessage = new CdpMessage();
        Report report = new Report();
        report.resourcePath = "uplinkMsg/0/data";
        report.value = "01020304";
        cdpMessage.reports.add(report);
        
        CdpRestApi api = new CdpRestApi();
        api.uplink(cdpMessage);
    }

}
