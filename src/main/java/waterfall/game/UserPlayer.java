package waterfall.game;

public class UserPlayer implements Player {

    private String mark;

    @Override
    public String makeMove(Game game, Move move) {
        return game.playMove(move, this);
    }

    @Override
    public String getMark() {
        return this.mark;
    }

    @Override
    public void setMark(String mark) {
        this.mark = mark;
    }
}
