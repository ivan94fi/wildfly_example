package rest.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("users")
public class UserEndpoint {

    @GET
    public Response getAll() {
        return Response.ok().entity("getAll() invoked").build();
    }

}
