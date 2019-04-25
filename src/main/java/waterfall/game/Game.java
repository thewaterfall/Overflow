package waterfall.game;

public interface Game {
    public String playMove(Move move, Player player);

    public boolean registerPlayer(Player player);

    public boolean unregisterPlayer(Player player);

    public Move convertToMove(String coordsMove);

    public Board getBoard();

    public Player getWinner();

    public boolean isFinished();

    public boolean isReady();

    public void start();
}
