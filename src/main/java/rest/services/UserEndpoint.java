package rest.services;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
        userMapper.transfer(dto, user);
        userDao.save(user);
        return Response.ok(userMapper.convert(user)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleUser(@PathParam("id") Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return Response.ok().entity("").build();
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

    @GET
    @Path("/insert-test-users")
    @Produces(MediaType.TEXT_HTML)
    public Response insertTestUser() {
        /* ********************* */
        User user1 = new User("test_user1");
        User user2 = new User("test_user2");
        LocalDateTime now = LocalDateTime.now();
        user1.setBookings(Arrays.asList(
                new Booking(now, now.plusDays(1), now.plusDays(8), null),
                new Booking(now, now.plusDays(15), now.plusDays(25), null)));
        user2.setBookings(Arrays.asList(
                new Booking(now, now.plusDays(1), now.plusDays(8), null),
                new Booking(now, now.plusDays(15), now.plusDays(25), null)));
        userDao.save(user1);
        userDao.save(user2);
        /* ********************* */
        // Arrays.asList(user1, user2).forEach(user -> {
        // try {
        // user.setBookings(userDao.getAllBookings(user.getId()));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // });
        return Response.ok().build();
    }

}
