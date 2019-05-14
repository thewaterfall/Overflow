package waterfall.game;

public interface GameFactory {
    public Player getPlayer(String name);

    public Game getGame(String name);

    public void register(String name, Class classType);
}
