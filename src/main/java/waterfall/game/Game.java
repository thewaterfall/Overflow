package waterfall.game;

public interface Game {
    public String playMove(Move move, Player player);

    public boolean registerPlayer(Player player);

    public Move convertToMove(String coordsMove);

    public Board getBoard();

    public Player getWinner();

    public boolean isFinished();
}
