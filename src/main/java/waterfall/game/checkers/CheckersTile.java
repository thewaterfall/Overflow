package waterfall.game.checkers;

import waterfall.game.Color;
import waterfall.game.Piece;
import waterfall.game.Tile;

public class CheckersTile implements Tile {

    private CheckersPiece piece;
    private Color color;

    public enum TileColor {
        White, Black
    }

    public CheckersTile() {

    }

    public CheckersTile(Color color){
        this.color = color;
    }

    public CheckersTile(Color color, CheckersPiece piece){
        this.color = color;
        this.piece = piece;
    }

    public void setPiece(CheckersPiece piece){
        this.piece = piece;
    }

    public CheckersPiece getPiece(){
        return this.piece;
    }

    public String getValue(){
        if(piece != null){
            return "[" + piece.getCharValue() + "]";
        } else {
            return "[ ]";
        }
    }

    public void empty(){
        piece = null;
    }

    public Color getColor() {
        return color;
    }

    public boolean isEmpty(){
        return piece == null;
    }

}
