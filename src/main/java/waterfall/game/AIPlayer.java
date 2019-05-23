package waterfall.game;

import waterfall.game.chess.MinimaxAlphaBeta;

public class AIPlayer implements Player {
    private MinimaxAlphaBeta algorithm;
    private String mark;

    public AIPlayer() {
        algorithm = new MinimaxAlphaBeta(this, 1);
    }

    @Override
    public String makeMove(Game game, Move move) {
        return game.playMove(algorithm.decision(game), this);
    }

    @Override
    public String getMark() {
        return mark;
    }

    @Override
    public void setMark(String mark) {
        this.mark = mark;
    }
}
