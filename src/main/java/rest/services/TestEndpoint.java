package rest.services;

import java.time.LocalDateTime;
import java.util.Arrays;

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
        return Response.ok().build();
    }

}
