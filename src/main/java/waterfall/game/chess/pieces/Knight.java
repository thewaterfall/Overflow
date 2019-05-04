package waterfall.game.chess.pieces;

import waterfall.game.Color;
import waterfall.game.chess.ChessPiece;
import waterfall.game.chess.MoveRule;

public class Knight extends ChessPiece{
	private final static float cost = 3;

	public Knight(Color color){
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

	@Override
	public float getCost() {
		return cost;
	}
}
