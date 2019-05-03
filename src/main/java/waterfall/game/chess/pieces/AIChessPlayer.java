package waterfall.game.chess.pieces;

import waterfall.game.Game;
import waterfall.game.Move;
import waterfall.game.Player;
import waterfall.game.chess.MinimaxAlphaBeta;

public class AIChessPlayer implements Player {
    private String mark;
    private MinimaxAlphaBeta minimax;

    public AIChessPlayer() {

    }

    @Override
    public String makeMove(Game game, Move move) {
        if(minimax == null) {
            minimax = new MinimaxAlphaBeta(this, 1);
        }

        Move chosenMove = minimax.decision(game);
        return game.playMove(chosenMove, this);
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
