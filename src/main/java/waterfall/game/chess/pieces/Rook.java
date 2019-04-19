package waterfall.game.chess.pieces;

import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class Rook extends ChessPiece {

	public Rook(PieceColor color){
		super(PieceType.Rook, color, validMoves(), true);
	}


	private static MoveRule[] validMoves(){
		return new MoveRule[]{	new MoveRule(1, 0, false, false), new MoveRule(0, 1, false, false),
                            new MoveRule(-1, 0, false, false), new MoveRule(0, -1, false, false)};
	}
}
