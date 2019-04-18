package waterfall.service;

import waterfall.dao.GameTypeDAO;
import waterfall.model.GameType;

import java.util.List;

public class GameTypeServiceImpl implements  GameTypeService {

    private GameTypeDAO gameTypeDAO;

    @Override
    public void save(GameType gameType) {
        gameTypeDAO.save(gameType);
    }

    @Override
    public void remove(GameType gameType) {
        gameTypeDAO.remove(gameType);
    }

    @Override
    public void update(GameType gameType) {
        gameTypeDAO.update(gameType);
    }

    @Override
    public GameType findById(Integer id) {
        return gameTypeDAO.findById(id);
    }

    @Override
    public GameType findByName(String name) {
        return gameTypeDAO.findByName(name);
    }

    @Override
    public List<GameType> findAll() {
        return gameTypeDAO.findAll();
    }
}
