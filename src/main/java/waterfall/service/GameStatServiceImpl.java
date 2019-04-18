package waterfall.service;

import waterfall.dao.GameStatDAO;
import waterfall.model.GameStat;
import waterfall.model.User;

import java.util.List;

public class GameStatServiceImpl implements GameStatService {

    private GameStatDAO gameStatDAO;

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
}
