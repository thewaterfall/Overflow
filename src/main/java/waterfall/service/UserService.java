package waterfall.service;

import waterfall.model.User;

import java.util.List;

public interface UserService {
    public void save(User user);

    public void remove(User user);

    public void update(User user);

    public User findById(Integer id);

    public User findByUsername(String username);

    public List<User> findAll();
}
