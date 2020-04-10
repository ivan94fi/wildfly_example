package daos;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Status;
import javax.transaction.SystemException;

import domain.Booking;
import domain.User;

@RequestScoped
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) throws DAOException {
        User result;
        try {
            getTransaction().begin();
            result = em.createQuery(
                    "from User " + "where username = :username ", User.class)
                       .setParameter("username", username)
                       .getSingleResult();
            getTransaction().commit();
        } catch (Exception e) {
            try {
                if (getTransaction() != null
                        && getTransaction().getStatus() == Status.STATUS_ACTIVE) {
                    getTransaction().rollback();
                }
            } catch (SystemException se) {
                // nothing to do
            }
            throw new DAOException(e);
        }
        return result;
    }

    public List<Booking> getAllBookings(Long id) throws DAOException {
        User result;
        try {
            getTransaction().begin();
            result = em.createQuery("select user " + "from User user "
                    + "left join fetch user.bookings " + "where user.id = :id",
                    User.class).setParameter("id", id).getSingleResult();
            getTransaction().commit();
        } catch (Exception e) {
            try {
                if (getTransaction() != null
                        && getTransaction().getStatus() == Status.STATUS_ACTIVE) {
                    getTransaction().rollback();
                }
            } catch (SystemException se) {
                // nothing to do
            }
            throw new DAOException(e);
        }
        return result.getBookings();
    }

}
