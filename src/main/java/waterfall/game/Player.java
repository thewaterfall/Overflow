package waterfall.game;

public interface Player {
    public String makeMove(Game game, Move move);

    public String getMark();

    public void setMark(String mark);
}
