package dtos;

import java.util.List;
import java.util.Set;

public class UserDTO {

    private String username;
    private List<BookingDTO> bookings;
    private Set<String> roles;
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<BookingDTO> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingDTO> bookings) {
        this.bookings = bookings;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

}
