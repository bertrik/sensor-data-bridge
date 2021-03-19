package nl.bertriksikken.nbiot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.nbiot.CdpMessage.Report;

public final class CdpRestApi implements ICdpRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(CdpRestApi.class);

    @Override
    public void uplink(CdpMessage cdpMessage) {
        LOG.info("Received CDP message: {}", cdpMessage);
        List<Report> reports = cdpMessage.reports;
        if (reports.size() == 1) {
            Report report = reports.get(0);
            if (report.resourcePath.equals("uplinkMsg/0/data")) {
                byte[] data = HexConverter.fromString(report.value);
                LOG.info("Received bytes: {}", data);
            }
        }
    }

    @Override
    public String ping() {
        LOG.info("ping received, sending pong");
        return "pong!";
    }

}
