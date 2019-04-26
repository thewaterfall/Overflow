package waterfall.game.chess;

import waterfall.game.Color;
import waterfall.game.Tile;

public class ChessTile implements Tile {

    private ChessPiece piece;
    private Color color;

    public enum TileColor {
        White, Black
    }

    public ChessTile() {

    }

    public ChessTile(Color color){
        this.color = color;
    }

    public ChessTile(Color color, ChessPiece piece){
        this.color = color;
        this.piece = piece;
    }

    public void setPiece(ChessPiece piece){
        this.piece = piece;
    }

    public ChessPiece getPiece(){
        return this.piece;
    }

    public String getValue(){
        if(piece != null){
            return "[" + piece.getCharValue() + "]";
        } else {
            return "[ ]";
        }
    }

    public Color getColor() {
        return color;
    }

    public boolean isEmpty(){
        return piece == null;
    }

    public void empty(){
        piece = null;
    }
}
