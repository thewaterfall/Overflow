package waterfall.game.chess;

import waterfall.game.Color;
import waterfall.game.Piece;

public class ChessPiece implements Piece {
    private PieceType type;
    private Color color;
    private MoveRule[] moveRules;
    private String name;
    private char charValue;
    private boolean repeatableMoves;

    public ChessPiece() {

    }

    protected ChessPiece(PieceType type, Color color, MoveRule[] moveRules, boolean repeatableMoves){
        this.type = type;
        this.color = color;
        this.moveRules = moveRules;
        this.repeatableMoves = repeatableMoves;
        name = type.name();
        charValue = type.name().trim().charAt(0);
    }

    public enum PieceType {
        Pawn, Rook, Knight, Bishop, Queen, King
    }

    public MoveRule[] getMoveRules(){ return moveRules; }

    public String getName(){ return name; }

    public Color getColor(){ return color; }

    public char getCharValue(){ return charValue; }

    public boolean hasRepeatableMoves(){ return repeatableMoves; }

    public PieceType getPieceType() {return type; }

    public static Color opponent(Color color) {
        return (color == Color.Black) ? Color.White : Color.Black;
    }

}
