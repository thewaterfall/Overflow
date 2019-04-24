package waterfall.game.chess;

public class MoveRule {
    public int x;
    public int y;
    public boolean firstMoveOnly;
    public boolean onTakeOnly;

    public MoveRule() {

    }

    public MoveRule(int x, int y, boolean firstMoveOnly, boolean onTakeOnly) {
        this. x = x;
        this. y = y;
        this.firstMoveOnly = firstMoveOnly;
        this.onTakeOnly = onTakeOnly;
    }
}
