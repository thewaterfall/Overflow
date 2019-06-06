package waterfall.game.checkers;

import waterfall.game.*;
import waterfall.game.checkers.pieces.Normal;

/**
 * The representation is a 8x8 grid where
 * A[row][col] marks the position of the checker piece.
 * Smoke starts at the "bottom" and Fire starts at the "top".
 * The top-left-most square is row = 0, column = 0.
 * @author Ayamin
 *
 */
public class CheckersBoard implements Board {

	// CheckersBoard properties and representation
	public static final int BOARD_SIZE = 8;
	private CheckersTile[][] representation;


	public CheckersBoard() {
		init();
	}

	@Override
	public CheckersTile[][] getBoardArray() {
		return representation;
	}

	/**
	 * Initialize the board putting checker pieces in their starting locations
	 */
	private void init() {
		representation = new CheckersTile[BOARD_SIZE][BOARD_SIZE];

		for (int row = 0; row < 3; row++){
			for (int col = 0; col < 4; col++) {
				CheckersTile white_piece = new CheckersTile(Color.White, new Normal(Color.White));
				CheckersTile black_piece = new CheckersTile(Color.Black, new Normal(Color.Black));
				representation[BOARD_SIZE - row - 1][2*col+ (row % 2)] = white_piece;
				representation[row][2*col + (BOARD_SIZE - 1 - row) %2] = black_piece;
			}
		}
	}

	/**
	 * Tests board equality.
	 * @param other The other board.
	 * @return true if the board's pieces are all equal to the other board's pieces.
	 */
	public boolean equals(CheckersBoard other) {
		for (int i = 0; i < BOARD_SIZE; ++i) {
			for (int j = 0; j < BOARD_SIZE; ++j) {
				if(!(this.representation[i][j]).equals(other.getBoardArray()[i][j]))
					return false;
			}
		}
		return true;
	}
	
	public CheckersPiece getPiece(Coordinates location) {
		return representation[location.getX()][location.getY()].getPiece();
	}

	public int countPiecesFor(Color color) {
		int count = 0;

		for(int i = 0; i<BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if(!getBoardArray()[i][j].isEmpty() && getBoardArray()[i][j].getPiece().getColor().equals(color)) {
					count++;
				}
			}
		}

		return count;
	}
}

