package waterfall.service;

import waterfall.model.GameType;

import java.util.List;

public interface GameTypeService {
    public void save(GameType gameType);

    public void remove(GameType gameType);

    public void update(GameType gameType);

    public GameType findById(Integer id);

    public GameType findByName(String name);

    public List<GameType> findAll();
}
