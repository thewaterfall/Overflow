package waterfall.game;

public interface Player {
    public String makeMove(Game game, Coordinates coords);

    public String getMark();
}
