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
		fillBoard();
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

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((j + i) % 2 == 0)
					representation[j][i] = new CheckersTile(Color.Black);
				else
					representation[j][i] = new CheckersTile(Color.White);
			}
		}
	}

	private void fillBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				if ((j + i) % 2 != 0)
					representation[j][i].setPiece(new Normal(Color.Black));
			}
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 5; j < 8; j++) {
				if ((j + i) % 2 != 0)
					representation[j][i].setPiece(new Normal(Color.White));
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

