package waterfall.game.chess.pieces;

import waterfall.game.Color;
import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class Bishop extends ChessPiece {

	public Bishop(Color color){
		super(PieceType.Bishop, color, validMoves(), true);
	}


	private static MoveRule[] validMoves(){
		return	new MoveRule[]{	new MoveRule(1, 1, false, false), new MoveRule(1, -1, false, false),
	                        new MoveRule(-1, 1, false, false), new MoveRule(-1, -1, false, false)};
	}
}
