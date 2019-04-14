package waterfall.dao;

import waterfall.model.User;

import java.util.List;

public interface UserDAO {
    public void save(User user);

    public void remove(User user);

    public void update(User user);

    public User findById(Integer id);

    public User findByUsername(String username);

    public List<User> findAll();
}
