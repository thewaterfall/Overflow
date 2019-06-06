package waterfall.game.checkers;

import waterfall.game.Color;
import waterfall.game.Piece;

public class CheckersPiece implements Piece {
	public Color color;
	private PieceType type;
	private String name;
	private char charValue;

	public enum PieceType {
		Normal, King
	}

	public CheckersPiece() {

	}

	public CheckersPiece(PieceType type, Color color) {
		this.type = type;
		this.color = color;
		name = type.name();
		charValue = type.name().trim().charAt(0);
	}

	
	public CheckersPiece(CheckersPiece other) {
		this.color = other.getColor();
		this.type = other.getType();
	}
	
	public PieceType getType() {
		return this.type;
	}
	
	public Color getColor() {
		return this.color;
	}

	@Override
	public float getCost() {
		return 0;
	}

	public char getCharValue(){ return charValue; }

	public boolean equals(CheckersPiece other) {
		return this.color == other.color &&
				this.type == other.getType();
	}

}
