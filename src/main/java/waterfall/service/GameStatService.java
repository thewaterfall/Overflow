package waterfall.service;

import waterfall.model.GameStat;
import waterfall.model.User;

import java.util.List;

public interface GameStatService {
    public void save(GameStat gameStat);

    public void remove(GameStat gameStat);

    public void update(GameStat gameStat);

    public GameStat findById(Integer id);

    public GameStat findByUser(User user);

    public List<GameStat> findAll();
}
