package waterfall.game.chess.pieces;

import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class Bishop extends ChessPiece {

	public Bishop(PieceColor color){
		super(PieceType.Bishop, color, validMoves(), true);
	}


	private static MoveRule[] validMoves(){
		return	new MoveRule[]{	new MoveRule(1, 1, false, false), new MoveRule(1, -1, false, false),
	                        new MoveRule(-1, 1, false, false), new MoveRule(-1, -1, false, false)};
	}
}
