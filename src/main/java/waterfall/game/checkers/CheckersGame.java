package waterfall.game.checkers;


import waterfall.game.*;
import waterfall.game.checkers.pieces.King;

import java.util.ArrayList;
import java.util.List;

public class CheckersGame implements Game{
	private boolean isFinished;
	private Color currentPlayer;
	private Player player;
	private Player playerWinner;
	private List<Player> playerList;
	private CheckersBoard board;
	private boolean inJumpSequence;

	public CheckersGame() {
		playerList = new ArrayList<Player>(2);
	}

	@Override
	public String playMove(Move move, Player player) {
		String message;

		if(isValidMove(move, player)) {
			movePiece(move);
			endTurn();

			message = "Moved from " + move.getStart() + " to" + move.getDestination();
		} else {
			message = "Invalid move";
		}

		return message;
	}

	@Override
	public boolean registerPlayer(Player player) {
		boolean isAdded = playerList.add(player);
		player.setMark(getMark());

		return isAdded;
	}

	@Override
	public boolean unregisterPlayer(Player player) {
		return playerList.remove(player);
	}

	@Override
	public Move convertToMove(String coordsMove) {
		return null;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public Player getWinner() {
		return playerWinner;
	}

	@Override
	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public boolean isReady() {
		return playerList.size() == 2;
	}

	@Override
	public void start() {
		this.player = playerList.get(0);
		board = new CheckersBoard();
		currentPlayer = Color.White;
		isFinished = false;
	}

	public String getMark() {
		if (getPlayer().equals(Color.White.name())) {
			return Color.Black.name();
		} else {
			return Color.White.name();
		}
	}

	private Player getPlayer() {
		if (playerList.get(0) != null) {
			return playerList.get(0);
		} else {
			return playerList.get(1);
		}
	}

	public Player getOpponent(Player player) {
		if (playerList.get(0).equals(player)) {
			return playerList.get(1);
		} else {
			return playerList.get(0);
		}
	}

	private void endTurn() {
		if(board.countPiecesFor(currentPlayer) > board.countPiecesFor(opposite(currentPlayer))) {
			isFinished = true;
			playerWinner = this.player;
		}

		if(!inJumpSequence) {
			currentPlayer = (currentPlayer == Color.White)
					? Color.Black
					: Color.White;

			player = getOpponent(player);
		}
	}

	public boolean isJump(Move move) {
		return Math.abs(move.getStart().getX() - move.getDestination().getX()) == 2
				&& Math.abs(move.getStart().getY() - move.getDestination().getY()) == 2;
	}

	public void movePiece(Move move) {
		int sourceRow = move.getStart().getX();
		int sourceCol = move.getStart().getY();
		int destRow = move.getDestination().getX();
		int destCol = move.getDestination().getY();

		CheckersTile fromTile = board.getBoardArray()[sourceRow][sourceCol];
		CheckersTile toTile = board.getBoardArray()[destRow][destCol];

		if (isJump(move)) {
			int monkeyRow = (destRow + sourceRow)/2;
			int monkeyCol = (destCol + sourceCol)/2;

			CheckersTile removed =  board.getBoardArray()[monkeyRow][monkeyCol];

			/* Remove the piece being jumped ("monkey in the middle") */
			removed.empty();
		}

		/* Place the piece in the destination cell */
		toTile.setPiece(fromTile.getPiece());

		/* Remove from the source cell */
		fromTile.empty();

		CheckersPiece moved = toTile.getPiece();

		if (canPromote(moved, move.getDestination())) {
			toTile.setPiece(promote(moved));
		}

		/* If this is the first jump of the jump sequence */
		if (isJump(move) && !inJumpSequence) {
			inJumpSequence = true;
		}

		if (movePromotesPiece(move)) {
			inJumpSequence = false;
		}

		if(getAvailableMoves(move.getStart()).isEmpty()) {
			inJumpSequence = false;
		}
	}

	public ArrayList<Move> getAvailableMoves(Coordinates source) {
		if (inJumpSequence) {
			return generateJumpMovesForPiece(board.getPiece(source), source);
		} else {
			return generateMovesForPiece(board.getPiece(source), source);
		}
	}

	/**
	 * Returns the possible jumps a piece can take.
	 */
	public ArrayList<Move> generateJumpMovesForPiece(CheckersPiece p, Coordinates location) {
		ArrayList<Move> jumps = new ArrayList<Move>();
		int row = location.getX(),
				col = location.getY();
		boolean up = p.getColor() == Color.White || p instanceof King;
		boolean down = p.getColor() == Color.Black || p instanceof King;
		if (up) {
			// Up left
			Move upleft = new Move(new Coordinates(row, col), new Coordinates(row - 2, col - 2));
			if (isValidJump(upleft)) {
				jumps.add(upleft);
			}
			// Up right
			Move upright = new Move(new Coordinates(row, col), new Coordinates(row - 2, col + 2));
			if (isValidJump(upright)) {
				jumps.add(upright);
			}
		}
		if (down) {
			// Down left
			Move downleft = new Move(new Coordinates(row, col), new Coordinates(row + 2, col - 2));
			if (isValidJump(downleft)) {
				jumps.add(downleft);
			}
			// Down right
			Move downright = new Move(new Coordinates(row, col), new Coordinates(row + 2, col + 2));
			if (isValidJump(downright)) {
				jumps.add(downright);
			}
		}
		return jumps;
	}

	public ArrayList<Move> generateMovesForPiece(CheckersPiece p, Coordinates location) {
		ArrayList<Move> jumps = generateJumpMovesForPiece(p, location);
		if (jumps.isEmpty()) {
			return generateRegularMovesForPiece(p, location);
		}
		return jumps;
	}

	/**
	 * Generates the Move set for a particular piece.
	 */
	public ArrayList<Move> generateRegularMovesForPiece(CheckersPiece p, Coordinates location) {
		ArrayList<Move> avail_moves = new ArrayList<Move>();
		int row = location.getX();
		int col = location.getY();
		boolean up, down;
		up = p.getColor() == Color.White || p instanceof King;
		down = p.getColor() == Color.Black || p instanceof King;
		if (up) {
			// up left
			Move upLeft = new Move(location, new Coordinates(row - 1, col - 1));
			if (isValidMove(upLeft, this.player)) {
				avail_moves.add(upLeft);
			}

			// up right
			Move upRight = new Move(location, new Coordinates(row - 1, col + 1));
			if (isValidMove(upRight, this.player)) {
				avail_moves.add(upRight);
			}
		}
		if (down) {
			// down left
			Move downLeft = new Move(location, new Coordinates(row + 1, col - 1));
			if (isValidMove(downLeft, this.player)) {
				avail_moves.add(downLeft);
			}

			// down right
			Move downRight = new Move(location, new Coordinates(row + 1, col + 1));
			if (isValidMove(downRight, this.player)) {
				avail_moves.add(downRight);
			}
		}
		return avail_moves;
	}

	public boolean isValidSquare(Coordinates location) {
		return 	0 <= location.getX() && location.getX() < CheckersBoard.BOARD_SIZE &&
				0 <= location.getY() && location.getY() < CheckersBoard.BOARD_SIZE;
	}

	/**
	 * Determines whether a move is a valid jump.
	 * @param move
	 * @return
	 */
	public boolean isValidJump(Move move) {
		if (isValidSquare(move.getDestination())) {
			CheckersPiece monkey = board.getBoardArray()[(move.getDestination().getX() + move.getStart().getX())/2][(move.getDestination().getY() + move.getStart().getY())/2].getPiece();
			CheckersPiece toMove = board.getBoardArray()[move.getStart().getX()][move.getStart().getY()].getPiece();
			return !isOccupied(move.getDestination())
					&& monkey != null
					&& monkey.getColor() == opposite(toMove.getColor());

		} else {
			return false;
		}
	}

	public boolean isValidMove(Move move, Player player) {
		return isValidSquare(move.getDestination()) && !isOccupied(move.getDestination()) && player == this.player;
	}

	/**
	 * return true if square contains a piece
	 * return false otherwise
	 */
	public boolean isOccupied(Coordinates location) {
		return !board.getBoardArray()[location.getX()][location.getY()].isEmpty();
	}

	public boolean canPromote(CheckersPiece p, Coordinates location) {

		return !(p instanceof King) &&
				isPromotionLocation(location);
	}

	public boolean movePromotesPiece(Move move) {
		return !(board.getPiece(move.getStart()) instanceof King) &&
				isPromotionLocation(move.getDestination());
	}

	public boolean isPromotionLocation(Coordinates location) {
		return (location.getX() == 0 ||
				location.getX() == CheckersBoard.BOARD_SIZE - 1 );
	}

	public CheckersPiece promote(CheckersPiece piece) {
		return new King(piece.getColor());
	}

	public Color opposite(Color color) {
		if(color.equals(Color.White)) return Color.Black;
		if(color.equals(Color.Black)) return Color.White;
		return null;
	}
}
