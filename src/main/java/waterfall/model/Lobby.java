package waterfall.model;

import javax.persistence.*;

@Entity
@Table(name = "lobby")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firstuser_id")
    private User firstUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seconduser_id")
    private User secondUser;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gametype_id")
    private GameType gameType;

    public Lobby() {

    }

    public Lobby(Integer id, User firstUser, User secondUser, GameType gameType) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.gameType = gameType;
    }

    public boolean isLobbyFull() {
        return firstUser != null && secondUser != null;
    }

    public void setToVacantSlot(User user) {
        if (!isLobbyFull()) {
            if (firstUser == null) {
                firstUser = user;
            } else {
                secondUser = user;
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
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
                lobby.getFirstUser().equals(this.firstUser) && lobby.getSecondUser().equals(this.secondUser)) {
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
        result = result * constant + this.firstUser.hashCode();
        result = result * constant + this.secondUser.hashCode();
        result = result * constant + this.gameType.hashCode();


        return result;
    }
}
