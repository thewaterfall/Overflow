package waterfall.dao;

import waterfall.model.GameType;
import waterfall.model.Lobby;
import waterfall.model.User;

// TODO implement methods
public class LobbyDAOImpl extends AbstractDAO<Integer, Lobby> implements LobbyDAO {

    public LobbyDAOImpl() {
        super(Lobby.class);
    }

    @Override
    public Lobby findByUser(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lobby findByGameType(GameType gameType) {
        throw new UnsupportedOperationException();
    }
}
