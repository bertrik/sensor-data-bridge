package nl.bertriksikken.nbiot;

import java.io.IOException;
import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.core.UriBuilder;

public final class NbIotReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(NbIotReceiver.class);

    private final Server server;

    public NbIotReceiver(NbIotConfig config) {
        this.server = createRestServer(config.getPort(), CdpRestApi.class);
    }

    public void start() throws IOException {
        LOG.info("Starting NB-IOT server");
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void stop() {
        LOG.info("Stopping NB-IOT server");
        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("Caught exception during shutdown: {}", e.getMessage());
            LOG.trace("Caught exception during shutdown", e);
        }
    }

    private Server createRestServer(int port, Class<?> clazz) {
        URI uri = UriBuilder.fromUri("http://localhost").port(port).build();
        ResourceConfig config = new ResourceConfig(clazz);
        return JettyHttpContainerFactory.createServer(uri, config);
    }

}
