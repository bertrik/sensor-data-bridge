package nl.bertriksikken.nbiot;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CdpRestServer implements ICdpRestApi {
    
    private static final Logger LOG = LoggerFactory.getLogger(CdpRestServer.class);

    private final Server server;
    
    public static void main(String[] args) throws Exception {
        CdpRestServer restServer = new CdpRestServer();
        restServer.start();
    }
    
    public CdpRestServer() throws Exception {
        this.server = createRestServer(9000, "", this.getClass());
    }
    
    public void start() throws Exception {
        server.start();
    }

    private Server createRestServer(int port, String contextPath, Class<?> clazz) throws Exception {
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
    
    @Override
    public void upload(CdpMessage cdpMessage) {
        LOG.info("upload");
    }

    @Override
    public String ping() {
        LOG.info("ping received, sending pong");
        return "pong!";
    }


}
