package nl.bertriksikken.nbiot;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/nbiot")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public interface ICdpRestApi {

    @POST
    @Path("/uplink")
    public void uplink(CdpMessage cdpMessage);

    @GET
    @Path("/ping")
    String ping();

}
