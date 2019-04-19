package waterfall.game;

public interface Game {
    public String playMove();

    public Board getBoard();

    public Player getWinner();

    public boolean isFinished();
}
