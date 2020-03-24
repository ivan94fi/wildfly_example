package daos;

import javax.enterprise.context.RequestScoped;

import domain.Booking;

@RequestScoped
public class BookingDAO extends BaseDAO<Booking> {

    public BookingDAO() {
        super(Booking.class);
    }
}
