package waterfall.game.chess.pieces;

import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class Knight extends ChessPiece{

	public Knight(ChessPiece.PieceColor color){
		super(PieceType.Knight, color, validMoves(), false);
	}


	private static MoveRule[] validMoves(){
		return new MoveRule[]{	new MoveRule(2, 1, false, false), new MoveRule(1, 2, false, false),
	                    	new MoveRule(2, -1, false, false), new MoveRule(-1, 2, false, false),
	                        new MoveRule(2, -1, false, false), new MoveRule(-1, 2, false, false),
	                        new MoveRule(-2, 1, false, false), new MoveRule(1, -2, false, false),
	                        new MoveRule(-2, -1, false, false), new MoveRule(-1, -2, false, false),
	                        new MoveRule(-2, -1, false, false), new MoveRule(-1, -2, false, false)};
	}
}
