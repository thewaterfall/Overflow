package waterfall.dao;

import waterfall.model.GameStat;
import waterfall.model.User;

public class GameStatDAOImpl extends AbstractDAO<Integer, GameStat> implements GameStatDAO {

    public GameStatDAOImpl() {
        super(GameStat.class);
    }

    @Override
    public GameStat findByUser(User user) {
        throw new UnsupportedOperationException();
    }
}
