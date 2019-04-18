package waterfall.dao;

import org.hibernate.query.Query;
import waterfall.model.User;
import waterfall.util.HibernateUtil;

public class UserDAOImpl extends AbstractDAO<Integer, User> implements UserDAO {
    public UserDAOImpl() {
        super(User.class);
    }

    @Override
    public User findByUsername(String username) {
        HibernateUtil.openSessionWithTransaction();
        Query<User> query = HibernateUtil.getCurrentSession().createQuery("FROM User WHERE username = :username")
                                                                .setParameter("username", username);
        HibernateUtil.closeSessionWithTransaction();

        return query.uniqueResult();
    }
}
