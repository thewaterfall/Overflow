package waterfall.dao;

import org.hibernate.query.Query;
import waterfall.model.GameStat;
import waterfall.model.User;
import waterfall.util.HibernateUtil;

public class GameStatDAOImpl extends AbstractDAO<Integer, GameStat> implements GameStatDAO {

    public GameStatDAOImpl() {
        super(GameStat.class);
    }

    @Override
    public GameStat findByUser(User user) {
        HibernateUtil.openSessionWithTransaction();
        Query<GameStat> query = HibernateUtil.getCurrentSession().createSQLQuery("SELECT * FROM gamestat " +
                "JOIN user_gamestat ON gamestat.id = user_gamestat.gamestat_id " +
                "WHERE user_gamestat.user_id = :id")
                .setParameter("id", user.getId());
        GameStat gameStat = query.uniqueResult();
        HibernateUtil.closeSessionWithTransaction();

        return gameStat;
    }
}
