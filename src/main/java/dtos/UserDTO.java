package dtos;

import java.util.List;

public class UserDTO {

    private String username;
    private List<Long> bookings;
    private Long id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Long> getBookings() {
        return bookings;
    }

    public void setBookings(List<Long> bookings) {
        this.bookings = bookings;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

}
