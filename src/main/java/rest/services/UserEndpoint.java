package rest.services;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import daos.UserDAO;
import domain.Booking;
import domain.User;
import dtos.UserDTO;
import mappers.UserMapper;

@Path("users")
public class UserEndpoint {

    @Inject
    private UserDAO userDao;

    @Inject
    private UserMapper userMapper;

    @Inject
    private Logger logger;

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = userDao.findAll();
        List<UserDTO> dtos = users.stream()
                                  .map(userMapper::convert)
                                  .collect(toList());
        return Response.ok().entity(dtos).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUser(UserDTO dto) {
        if (dto == null || !userMapper.isValid(dto)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        User user = new User();
        boolean saveSuccess = userDao.save(user);
        userMapper.transfer(dto, user);
        // TODO: is this right? Added otherwise the username is not saved inside
        // the entity..
        boolean mergeSuccess = userDao.merge(user);
        if (!saveSuccess || !mergeSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        URI uri;
        try {
            uri = new URI(user.getId().toString());
        } catch (URISyntaxException e) {
            logger.error(e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.created(uri).entity(userMapper.convert(user)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleUser(@PathParam("id") Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        List<Booking> userBookings;
        try {
            userBookings = userDao.getAllBookings(id);
        } catch (Exception e) {
            logger.error(e);
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                           .entity("Unable to retrieve bookings for user with id "
                                   + id)
                           .build();
        }
        user.setBookings(userBookings);
        return Response.ok(userMapper.convert(user)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("id") Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        UserDTO dto = userMapper.convert(user);
        boolean success = userDao.delete(user);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(dto).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, UserDTO dto) {
        User user = userDao.findById(id);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (dto == null || !userMapper.isValid(dto)) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        userMapper.transfer(dto, user);
        boolean success = userDao.merge(user);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(userMapper.convert(user)).build();
    }

}
