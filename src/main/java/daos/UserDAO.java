package daos;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import domain.Booking;
import domain.User;

@RequestScoped
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) throws Exception {
        transaction.begin();
        User result = em.createQuery(
                "from User " + "where username = :username ", User.class)
                        .setParameter("username", username)
                        .getSingleResult();
        transaction.commit();
        return result;
    }

    public List<Booking> getAllBookings(Long id) throws Exception {
        transaction.begin();
        User result = em.createQuery("select user " + "from User user "
                + "left join fetch user.bookings " + "where user.id = :id",
                User.class).setParameter("id", id).getSingleResult();
        transaction.commit();
        return result.getBookings();
    }

}
