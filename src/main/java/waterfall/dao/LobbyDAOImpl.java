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
        Query<Lobby> query = HibernateUtil.getCurrentSession().createQuery("FROM Lobby AS lobby JOIN lobby.users users " +
                "WHERE users.id = :id")
                .setParameter("id", user.getId());
        Lobby lobby = query.uniqueResult();
        HibernateUtil.closeSessionWithTransaction();

        return lobby;
    }

    @Override
    public Lobby findByGameType(GameType gameType) {
        HibernateUtil.openSessionWithTransaction();
        Query<Lobby> query = HibernateUtil.getCurrentSession().createQuery("FROM Lobby AS lobby JOIN lobby.gameType AS gametype " +
                "WHERE gametype.id = :id")
                .setParameter("id", gameType.getId());
        Lobby lobby = query.uniqueResult();
        HibernateUtil.closeSessionWithTransaction();

        return lobby;
    }
}
