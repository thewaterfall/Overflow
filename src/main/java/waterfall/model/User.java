package waterfall.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_gamestat", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "gamestat_id")})
    private Set<GameStat> gameStats;

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<GameStat> getGameStats() {
        return gameStats;
    }

    public void setGameStats(Set<GameStat> gameStats) {
        this.gameStats = gameStats;
    }

    public void addGameStat(GameStat gameStat) {
        this.gameStats.add(gameStat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof User))
            return false;

        User user = (User) obj;

        if (user.getId().equals(this.id) && user.getUsername().equals(this.username)) {
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
        result = result * constant + this.username.hashCode();

        return result;
    }
}
