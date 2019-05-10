package waterfall.dao;

import org.hibernate.query.Query;
import waterfall.model.GameType;
import waterfall.util.HibernateUtil;

public class GameTypeDAOImpl extends AbstractDAO<Integer, GameType> implements GameTypeDAO {

    public GameTypeDAOImpl() {
        super(GameType.class);
    }

    @Override
    public GameType findByName(String name) {
        HibernateUtil.openSessionWithTransaction();
        Query<GameType> query = HibernateUtil.getCurrentSession().createQuery("FROM GameType WHERE type = :name")
                .setParameter("name", name);
        GameType gameType = query.getSingleResult();
        HibernateUtil.closeSessionWithTransaction();

        return gameType;
    }
}
