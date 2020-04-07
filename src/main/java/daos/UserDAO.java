package daos;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Status;

import domain.Booking;
import domain.User;

@RequestScoped
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) throws Exception {
        User result;
        try {
            getTransaction().begin();
            result = em.createQuery(
                    "from User " + "where username = :username ", User.class)
                       .setParameter("username", username)
                       .getSingleResult();
            getTransaction().commit();
        } catch (Exception e) {
            if (getTransaction() != null
                    && getTransaction().getStatus() == Status.STATUS_ACTIVE) {
                getTransaction().rollback();
            }
            throw e;
        }
        return result;
    }

    public List<Booking> getAllBookings(Long id) throws Exception {
        User result;
        try {
            getTransaction().begin();
            result = em.createQuery("select user " + "from User user "
                    + "left join fetch user.bookings " + "where user.id = :id",
                    User.class).setParameter("id", id).getSingleResult();
            getTransaction().commit();
        } catch (Exception e) {
            if (getTransaction() != null
                    && getTransaction().getStatus() == Status.STATUS_ACTIVE) {
                getTransaction().rollback();
            }
            throw e;
        }
        return result.getBookings();
    }

}
