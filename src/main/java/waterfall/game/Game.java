package waterfall.game;

import java.util.List;

public interface Game {
    public String playMove();

    public Board getBoard();

    public Player getWinner();

    public List<Player> getPlayers();

    public boolean isFinished();
}
