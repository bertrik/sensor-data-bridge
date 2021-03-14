package nl.bertriksikken.nbiot;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/nbiot")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ICdpRestApi {

    @POST
    @Path("/uplink")
    public void upload(CdpMessage cdpMessage);

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    String ping();

}
