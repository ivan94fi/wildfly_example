package rest.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daos.StructureDAO;
import daos.UserDAO;
import domain.Booking;
import domain.Structure;
import domain.User;
import domain.User.Role;

@Path("test")
public class TestEndpoint {

    @Inject
    private StructureDAO structureDao;

    @Inject
    private UserDAO userDao;

    @GET
    @Path("/populate")
    @Produces(MediaType.TEXT_HTML)
    public Response insertTestUser() {
        Structure structure1 = new Structure("Hotel Stella Alpina",
                "Via Trento 1");
        Structure structure2 = new Structure("Hotel Bellosguardo",
                "Via Case Sparse 42");
        structureDao.save(structure1);
        structureDao.save(structure2);
        User user1 = new User("test_user1");
        User user2 = new User("test_user2");
        User user3 = new User("test_user3"); // do not set roles
        User user4 = new User("test_user4");
        user1.setRoles(EnumSet.of(Role.BASIC, Role.ADMIN));
        user2.setRoles(EnumSet.of(Role.BASIC, Role.MODERATOR));
        user4.setRoles(EnumSet.noneOf(Role.class));

        LocalDateTime now = LocalDateTime.now();
        user1.setBookings(Arrays.asList(
                new Booking(now, now.plusDays(1), now.plusDays(8), structure1),
                new Booking(now, now.plusDays(15), now.plusDays(25),
                        structure2)));
        user2.setBookings(Arrays.asList(
                new Booking(now, now.plusDays(1), now.plusDays(8), structure1),
                new Booking(now, now.plusDays(15), now.plusDays(25),
                        structure2)));
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);
        userDao.save(user4);
        return Response.ok().build();
    }

}
