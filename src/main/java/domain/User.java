package domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class User extends BaseEntity {

    public enum Role {
        ADMIN, BASIC, MODERATOR
    }

    private String username;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Booking> bookings;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        booking.setUser(this);
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        bookings.forEach(booking -> booking.setUser(this));
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User [username=" + username + "]";
    }

}
