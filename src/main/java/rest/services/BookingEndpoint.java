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

import daos.BookingDAO;
import domain.Booking;
import dtos.BookingDTO;
import mappers.BookingMapper;

@Path("bookings")
public class BookingEndpoint {

    @Inject
    private BookingDAO bookingDao;

    @Inject
    private BookingMapper bookingMapper;

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        List<Booking> bookings = bookingDao.findAll();
        List<BookingDTO> dtos = bookings.stream().map(bookingMapper::convert)
                .collect(toList());
        return Response.ok().entity(dtos).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postBooking(BookingDTO dto) {
        if (dto == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Booking booking = new Booking();
        boolean saveSuccess = bookingDao.save(booking);
        bookingMapper.transfer(dto, booking);
        boolean mergeSuccess = bookingDao.merge(booking);
        if (!saveSuccess || !mergeSuccess) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        URI uri;
        try {
            uri = new URI(booking.getId().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.created(uri).entity(bookingMapper.convert(booking))
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleBooking(@PathParam("id") Long id) {
        Booking booking = bookingDao.findById(id);
        if (booking == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(bookingMapper.convert(booking)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeBooking(@PathParam("id") Long id) {
        Booking booking = bookingDao.findById(id);
        if (booking == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        BookingDTO dto = bookingMapper.convert(booking);
        boolean success = bookingDao.delete(booking);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(dto).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBooking(@PathParam("id") Long id, BookingDTO dto) {
        Booking booking = bookingDao.findById(id);
        if (booking == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (dto == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        bookingMapper.transfer(dto, booking);
        boolean success = bookingDao.merge(booking);
        if (!success) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(bookingMapper.convert(booking)).build();
    }

}
