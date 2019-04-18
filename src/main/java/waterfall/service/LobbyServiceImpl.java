package waterfall.service;

import waterfall.dao.LobbyDAO;
import waterfall.model.GameType;
import waterfall.model.Lobby;
import waterfall.model.User;

import java.util.List;

public class LobbyServiceImpl implements LobbyService {

    private LobbyDAO lobbyDAO;

    @Override
    public void save(Lobby lobby) {
        lobbyDAO.save(lobby);
    }

    @Override
    public void remove(Lobby lobby) {
        lobbyDAO.remove(lobby);
    }

    @Override
    public void update(Lobby lobby) {
        lobbyDAO.update(lobby);
    }

    @Override
    public Lobby findById(Integer id) {
        return lobbyDAO.findById(id);
    }

    @Override
    public Lobby findByUser(User user) {
        return lobbyDAO.findByUser(user);
    }

    @Override
    public Lobby findByGameType(GameType gameType) {
        return lobbyDAO.findByGameType(gameType);
    }

    @Override
    public List<Lobby> findAll() {
        return lobbyDAO.findAll();
    }
}
