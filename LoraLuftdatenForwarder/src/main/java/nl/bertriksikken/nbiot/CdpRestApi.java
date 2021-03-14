package nl.bertriksikken.nbiot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CdpRestApi implements ICdpRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(CdpRestApi.class);

    @Override
    public void uplink(CdpMessage cdpMessage) {
        LOG.info("Received CDP message: {}", cdpMessage);
    }

    @Override
    public String ping() {
        LOG.info("ping received, sending pong");
        return "pong!";
    }
    
}
