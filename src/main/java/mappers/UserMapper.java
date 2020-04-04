package mappers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import daos.BookingDAO;
import daos.UserDAO;
import domain.Booking;
import domain.User;
import domain.User.Role;
import dtos.BookingDTO;
import dtos.UserDTO;

@RequestScoped
public class UserMapper {

    @Inject
    private UserDAO userDao;

    @Inject
    private BookingDAO bookingDao;

    @Inject
    private BookingMapper bookingMapper;

    public UserDTO convert(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        dto.setRoles(new HashSet<>());
        this.convertRoles(dto, user.getRoles());

        List<BookingDTO> bookingDtos;
        try {
            bookingDtos = userDao.getAllBookings(user.getId()).stream()
                    .map(bookingMapper::convert).collect(toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null; // FIXME
        }
        dto.setBookings(bookingDtos);
        return dto;
    }

    public void transfer(UserDTO dto, User user) {
        if (dto == null) {
            throw new IllegalArgumentException("dto cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        user.setUsername(dto.getUsername());

        if (user.getRoles() == null) {
            user.setRoles(EnumSet.noneOf(Role.class));
        }
        this.transferRoles(user, dto.getRoles());

        user.setBookings(new ArrayList<>());
        List<BookingDTO> bookingDtos = dto.getBookings();
        if (bookingDtos == null || bookingDtos.isEmpty()) {
            return;
        }
        for (BookingDTO bookingDto : bookingDtos) {
            bookingDto.setUser(user.getId());
            Booking booking = new Booking();
            bookingMapper.transfer(bookingDto, booking);
            user.addBooking(booking);
            bookingDao.save(booking);
        }
    }

    private void convertRoles(UserDTO dto, Set<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            roles.forEach(role -> dto.getRoles().add(role.toString()));
        } else {
            dto.getRoles().add(Role.BASIC.toString());
        }
    }

    private void transferRoles(User user, Set<String> roles) {
        user.getRoles().clear();
        if (roles != null && !roles.isEmpty()) {
            roles.forEach(role -> user.addRole(Role.valueOf(role)));
        } else {
            user.addRole(Role.BASIC);
        }
    }

}
