package waterfall.model;

import waterfall.game.Game;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lobby")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lobby_user", joinColumns = {@JoinColumn(name = "lobby_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gametype_id")
    private GameType gameType;

    @Transient
    private Game game;

    public Lobby() {

    }

    public Lobby(GameType gameType) {
        this.gameType = gameType;
        this.users = new HashSet<>(2);
    }

    public User getOpponentFor(User user) {
        for (User u : users) {
            if (!u.equals(user)) {
                return u;
            }
        }

        return null;
    }

    public boolean isLobbyFull() {
        return users.size() == 2;
    }

//    public void setToVacantSlot(User user) {
//        if (!isLobbyFull()) {
//            if (firstUser == null) {
//                firstUser = user;
//            } else {
//                secondUser = user;
//            }
//        }
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public boolean addUser(User user) {
        if (users.size() == 2) {
            return users.add(user);
        }

        return false;
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Lobby))
            return false;

        Lobby lobby = (Lobby) obj;

        if (lobby.getId().equals(this.id) && lobby.getGameType().equals(this.gameType) &&
                lobby.getUsers().equals(this.users)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        int constant = 11;

        result = result * constant + this.id.hashCode();
        result = result * constant + this.users.hashCode();
        result = result * constant + this.gameType.hashCode();


        return result;
    }
}
