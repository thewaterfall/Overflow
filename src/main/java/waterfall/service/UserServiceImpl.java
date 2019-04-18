package waterfall.service;

import waterfall.dao.UserDAO;
import waterfall.model.User;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    @Override
    public void save(User user) {
        userDAO.save(user);
    }

    @Override
    public void remove(User user) {
        userDAO.remove(user);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public User findById(Integer id) {
        return userDAO.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userDAO.findAll();
    }
}
