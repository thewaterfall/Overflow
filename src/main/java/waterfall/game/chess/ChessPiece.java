package waterfall.game.chess;

public abstract class ChessPiece {
    private final PieceType type;
    private final PieceColor color;
    private final MoveRule[] moveRules;
    private final String name;
    private final char charValue;
    private final boolean repeatableMoves;

    protected ChessPiece(PieceType type, PieceColor color, MoveRule[] moveRules, boolean repeatableMoves){
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

    public enum PieceColor {
        White, Black
    }
    public MoveRule[] getMoveRules(){ return moveRules; }

    public String getName(){ return name; }

    public PieceColor getColor(){ return color; }

    public char getCharValue(){ return charValue; }

    public boolean hasRepeatableMoves(){ return repeatableMoves; }

    public PieceType getPieceType() {return type; }

    public static PieceColor opponent(PieceColor color) {
        return (color == PieceColor.Black) ? PieceColor.White : PieceColor.Black;
    }

}
