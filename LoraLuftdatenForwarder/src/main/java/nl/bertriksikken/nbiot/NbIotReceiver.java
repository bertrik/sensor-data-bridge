package nl.bertriksikken.nbiot;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NbIotReceiver {
    
    private static final Logger LOG = LoggerFactory.getLogger(NbIotReceiver.class);

    private final Server server;
    
    public static void main(String[] args) throws Exception {
        NbIotReceiver restServer = new NbIotReceiver();
        restServer.start();
    }
    
    public NbIotReceiver() {
        this.server = createRestServer(9000, "", CdpRestApi.class);
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

    private Server createRestServer(int port, String contextPath, Class<?> clazz) {
        Server server = new Server(port);

        // setup context
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        
        // setup web services container
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, clazz.getCanonicalName());
        context.addServlet(sh, contextPath + "/*");
        server.setHandler(context);
        
        return server;
    }



}
