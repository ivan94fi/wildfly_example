package rest.services;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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

import daos.UserDAO;
import domain.User;
import dtos.UserDTO;
import mappers.UserMapper;

@Path("users")
public class UserEndpoint {

    @Inject
    private UserDAO userDao;

    @Inject
    private UserMapper userMapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<User> users = userDao.findAll();
        List<UserDTO> dtos = users.stream().map(userMapper::convert)
                .collect(toList());
        return Response.ok().entity(dtos).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUser(UserDTO dto) {
        if (dto == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        User user = new User();
        userDao.save(user);
        userMapper.transfer(dto, user);
        userDao.merge(user); // TODO: is this right? Added otherwise the
                             // username is not saved inside the entity..
        URI uri;
        try {
            uri = new URI(user.getId().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
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
        try {
            user.setBookings(userDao.getAllBookings(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
                    "Unable to retrieve bookings for user with id " + id)
                    .build();
        }
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
        userDao.delete(user);
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
        if (dto == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        userMapper.transfer(dto, user);

        if (userDao.merge(user)) {
            return Response.ok(userMapper.convert(user)).build();
        }
        return Response.status(Status.INTERNAL_SERVER_ERROR).build();

    }

}
