package waterfall.game.chess.pieces;

import waterfall.game.Color;
import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class King extends ChessPiece{

	public King(Color color){
		super(PieceType.King, color, validMoves(), false);
	}

	private static MoveRule[] validMoves(){
		return new MoveRule[]{	new MoveRule(1, 0, false, false), new MoveRule(0, 1, false, false),
                        	new MoveRule(-1, 0, false, false), new MoveRule(0, -1, false, false),
                        	new MoveRule(1, 1, false, false), new MoveRule(1, -1, false, false),
                        	new MoveRule(-1, 1, false, false), new MoveRule(-1, -1, false, false)};
	}
}
