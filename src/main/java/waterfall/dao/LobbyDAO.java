package waterfall.dao;

import waterfall.model.GameType;
import waterfall.model.Lobby;
import waterfall.model.User;

import java.util.List;

public interface LobbyDAO {
    public void save(Lobby lobby);

    public void remove(Lobby lobby);

    public void update(Lobby lobby);

    public Lobby findById(Integer id);

    public Lobby findByUser(User user);

    public Lobby findByGameType(GameType gameType);

    public List<Lobby> findAll();
}
