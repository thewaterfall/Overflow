package waterfall.service;

import com.google.inject.Inject;
import waterfall.dao.UserDAO;
import waterfall.model.GameType;
import waterfall.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Inject
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

    @Override
    public List<User> getLeaderboard(GameType gameType) {
        List<User> userList = new ArrayList<>();

        for (User user : userDAO.findAll()) {
            if (user.hasGameStat(gameType)) {
                userList.add(user);
            }
        }

        userList.sort(Comparator.comparing(o -> o.getGameStat(gameType).getWinAmount()));
        return userList;
    }
}
