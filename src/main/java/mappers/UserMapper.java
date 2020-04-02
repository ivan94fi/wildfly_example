package mappers;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import daos.BookingDAO;
import daos.UserDAO;
import domain.Booking;
import domain.User;
import dtos.UserDTO;

@RequestScoped
public class UserMapper {

    @Inject
    private UserDAO userDao;

    @Inject
    private BookingDAO bookingDao;

    public UserDTO convert(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        List<Long> bookingIds;
        try {
            bookingIds = userDao.getAllBookings(user.getId()).stream()
                    .map(Booking::getId).collect(toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null; // FIXME
        }
        dto.setBookings(bookingIds);
        return dto;
    }

    public void transfer(UserDTO dto, User user) {
        if (dto == null) {
            throw new IllegalArgumentException("dto cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        // TODO: checks on id?
        user.setUsername(dto.getUsername());

        List<Booking> bookings = dto.getBookings().stream()
                .map(bookingDao::findById).filter(Objects::nonNull)
                .collect(toList());

        user.setBookings(bookings);
    }

}
