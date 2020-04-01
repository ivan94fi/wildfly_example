package rest.services;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import daos.UserDAO;
import domain.User;

@Path("users")
public class UserEndpoint {

    @Inject
    private UserDAO userDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = userDao.findAll();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleUser(@PathParam("id") Long id) {
        User user = userDao.findById(id);
        if (user != null) {
            try {
                user.setBookings(userDao.getAllBookings(id));
            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
                        "Unable to retrieve bookings for user with id " + id)
                        .build();
            }
        }
        return Response.ok(user).build();
    }

}
