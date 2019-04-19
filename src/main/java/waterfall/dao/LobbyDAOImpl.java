package waterfall.dao;

import org.hibernate.query.Query;
import waterfall.model.GameType;
import waterfall.model.Lobby;
import waterfall.model.User;
import waterfall.util.HibernateUtil;

public class LobbyDAOImpl extends AbstractDAO<Integer, Lobby> implements LobbyDAO {

    public LobbyDAOImpl() {
        super(Lobby.class);
    }

    @Override
    public Lobby findByUser(User user) {
        HibernateUtil.openSessionWithTransaction();
        Query<Lobby> query = HibernateUtil.getCurrentSession().createQuery(" FROM Lobby WHERE firstuser_id = :id OR seconduser_id = :id")
                .setParameter("id", user.getId());
        HibernateUtil.closeSessionWithTransaction();

        return query.uniqueResult();
    }

    @Override
    public Lobby findByGameType(GameType gameType) {
        HibernateUtil.openSessionWithTransaction();
        Query<Lobby> query = HibernateUtil.getCurrentSession().createQuery(" FROM Lobby WHERE gametype_id = :id")
                .setParameter("id", gameType.getId());
        HibernateUtil.closeSessionWithTransaction();

        return query.uniqueResult();
    }
}
