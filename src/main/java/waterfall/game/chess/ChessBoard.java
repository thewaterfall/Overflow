package waterfall.game.chess;

import waterfall.game.Board;
import waterfall.game.Coordinates;
import waterfall.game.chess.pieces.*;

import java.util.ArrayList;

public class ChessBoard implements Board {
    private final Tile[][] boardArray;

    private final char alphaCoordinates[] = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

    public ChessBoard() {
        boardArray = new Tile[8][8];
        initializeBoard();
        fillBoard();
    }

    @Override
    public Object[][] getBoardArray() {
        return boardArray;
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((j + i) % 2 == 0) boardArray[i][j] = new Tile(Tile.TileColor.Black);
                else boardArray[i][j] = new Tile(Tile.TileColor.White);
            }
        }
    }

    //Will break on boards with no Kings of 'color'. Should never happen.
    public Coordinates getKingLocation(ChessPiece.PieceColor color) {
        Coordinates location = new Coordinates(-1, -1);
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (!boardArray[y][x].isEmpty()) {
                    ChessPiece piece = boardArray[y][x].getPiece();
                    if (piece.getColor() == color && piece instanceof King) {
                        location = new Coordinates(x, y);
                    }
                }
            }
        }
        return location;
    }

    public Coordinates[] getAllPiecesLocationForColor(ChessPiece.PieceColor color) {
        ArrayList<Coordinates> locations = new ArrayList<>();
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (!boardArray[y][x].isEmpty() && boardArray[y][x].getPiece().getColor() == color)
                    locations.add(new Coordinates(x, y));
            }
        }
        return locations.toArray(new Coordinates[0]);//allocate new array automatically.
    }

    public Tile getTileFromCoordinates(Coordinates coordinates) {
        return boardArray[coordinates.getY()][coordinates.getX()];
    }

    /*
    Initial filler of boardArray
     */
    private void fillBoard() {
        //pawns
        for (int i = 0; i < 8; i++) {
            boardArray[1][i].setPiece(new Pawn(ChessPiece.PieceColor.Black));
            boardArray[6][i].setPiece(new Pawn(ChessPiece.PieceColor.White));
        }

        //rooks
        boardArray[0][0].setPiece(new Rook(ChessPiece.PieceColor.Black));
        boardArray[0][7].setPiece(new Rook(ChessPiece.PieceColor.Black));
        boardArray[7][0].setPiece(new Rook(ChessPiece.PieceColor.White));
        boardArray[7][7].setPiece(new Rook(ChessPiece.PieceColor.White));

        //knight
        boardArray[0][1].setPiece(new Knight(ChessPiece.PieceColor.Black));
        boardArray[0][6].setPiece(new Knight(ChessPiece.PieceColor.Black));
        boardArray[7][1].setPiece(new Knight(ChessPiece.PieceColor.White));
        boardArray[7][6].setPiece(new Knight(ChessPiece.PieceColor.White));

        //bishop
        boardArray[0][2].setPiece(new Bishop(ChessPiece.PieceColor.Black));
        boardArray[0][5].setPiece(new Bishop(ChessPiece.PieceColor.Black));
        boardArray[7][2].setPiece(new Bishop(ChessPiece.PieceColor.White));
        boardArray[7][5].setPiece(new Bishop(ChessPiece.PieceColor.White));

        //queens
        boardArray[0][3].setPiece(new Queen(ChessPiece.PieceColor.Black));
        boardArray[7][3].setPiece(new Queen(ChessPiece.PieceColor.White));

        //kings
        boardArray[0][4].setPiece(new King(ChessPiece.PieceColor.Black));
        boardArray[7][4].setPiece(new King(ChessPiece.PieceColor.White));
    }

    @Override
    public String toString() {
        StringBuffer toReturn = new StringBuffer();
        toReturn.append("  0  1  2  3  4  5  6  7\n\n");

        for (int i = 0; i < boardArray.length; i++) {
            toReturn.append(alphaCoordinates[i] + "  ");
            for (int j = 0; j < boardArray.length; j++) {
                toReturn.append(boardArray[i][j].getValue() + "  ");
            }
            toReturn.append("\n\n");
        }

        return toReturn.toString();
    }
}
