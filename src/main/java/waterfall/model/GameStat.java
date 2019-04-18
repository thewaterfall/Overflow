package waterfall.model;

import javax.persistence.*;

@Entity
@Table(name = "gamestat")
public class GameStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gametype_id")
    private GameType gameType;

    @JoinColumn(name = "totalgames")
    private int totalGames;

    @JoinColumn(name = "winamount")
    private int winAmount;

    @JoinColumn(name = "loseamount")
    private int loseAmount;

    public GameStat() {

    }

    public GameStat(GameType gameType, int totalGames, int winAmount, int loseAmount) {
        this.gameType = gameType;
        this.totalGames = totalGames;
        this.winAmount = winAmount;
        this.loseAmount = loseAmount;
    }

    public void addWin() {
        totalGames++;
        winAmount++;
    }

    public void addLose() {
        totalGames++;
        loseAmount++;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public int getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(int winAmount) {
        this.winAmount = winAmount;
    }

    public int getLoseAmount() {
        return loseAmount;
    }

    public void setLoseAmount(int loseAmount) {
        this.loseAmount = loseAmount;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GameStat))
            return false;

        GameStat gameStat = (GameStat) obj;

        if (gameStat.getId().equals(this.id) && gameStat.getGameType().equals(this.gameType)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int constant = 11;
        int result = 1;

        result = result * constant + this.id.hashCode();
        result = result * constant + this.gameType.hashCode();

        return result;
    }
}
