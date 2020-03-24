package daos;

import javax.enterprise.context.RequestScoped;

import domain.User;

@RequestScoped
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }
}
