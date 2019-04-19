package waterfall.service;

import waterfall.dao.GameStatDAO;
import waterfall.dao.UserDAO;
import waterfall.model.GameStat;
import waterfall.model.GameType;
import waterfall.model.User;

import java.util.List;

public class GameStatServiceImpl implements GameStatService {

    private GameStatDAO gameStatDAO;
    private UserDAO userDAO;

    @Override
    public void save(GameStat gameStat) {
        gameStatDAO.save(gameStat);
    }

    @Override
    public void remove(GameStat gameStat) {
        gameStatDAO.remove(gameStat);
    }

    @Override
    public void update(GameStat gameStat) {
        gameStatDAO.update(gameStat);
    }

    @Override
    public GameStat findById(Integer id) {
        return gameStatDAO.findById(id);
    }

    @Override
    public GameStat findByUser(User user) {
        return gameStatDAO.findByUser(user);
    }

    @Override
    public List<GameStat> findAll() {
        return gameStatDAO.findAll();
    }

    @Override
    public boolean IsGameStatPresent(User user, GameType gameType) {
        User foundUser = userDAO.findById(user.getId());
        boolean isPresent = false;

        for (GameStat gameStat : foundUser.getGameStats()) {
            if (gameStat.getGameType().equals(gameType)) {
                isPresent = true;
                break;
            }
        }

        return isPresent;
    }

    @Override
    public GameStat findByUserAndGameType(User user, GameType gameType) {
        User foundUser = userDAO.findById(user.getId());
        GameStat foundGameStat = null;

        for (GameStat gameStat : foundUser.getGameStats()) {
            if (gameStat.getGameType().equals(gameType)) {
                foundGameStat = gameStat;
                break;
            }
        }

        return foundGameStat;
    }
}
