package waterfall.gui;

import waterfall.game.Board;

public interface GUI {
    public void write(String message);

    public String read();

    public void update();

    public void updateBoard(Board board);
}
