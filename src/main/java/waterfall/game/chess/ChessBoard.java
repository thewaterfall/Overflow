package waterfall.game.chess;

import waterfall.game.Board;
import waterfall.game.Color;
import waterfall.game.Coordinates;
import waterfall.game.chess.pieces.*;

import java.util.ArrayList;

public class ChessBoard implements Board {

    private final ChessTile[][] boardArray;

    private final char alphaCoordinates[] = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

    public ChessBoard() {
        boardArray = new ChessTile[8][8];
        initializeBoard();
        fillBoard();
    }

    public ChessBoard(ChessTile[][] tiles) {
        this.boardArray = tiles;
    }

    @Override
    public ChessTile[][] getBoardArray() {
        return boardArray;
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((j + i) % 2 == 0)
                    boardArray[i][j] = new ChessTile(Color.Black);
                else
                    boardArray[i][j] = new ChessTile(Color.White);
            }
        }
    }

    //Will break on boards with no Kings of 'color'. Should never happen.
    public Coordinates getKingLocation(Color color) {
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

    public Coordinates[] getAllPiecesLocationForColor(Color color) {
        ArrayList<Coordinates> locations = new ArrayList<>();
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (!boardArray[y][x].isEmpty() && boardArray[y][x].getPiece().getColor() == color)
                    locations.add(new Coordinates(x, y));
            }
        }
        return locations.toArray(new Coordinates[0]);//allocate new array automatically.
    }

    public ChessTile getTileFromCoordinates(Coordinates coordinates) {
        return boardArray[coordinates.getY()][coordinates.getX()];
    }

    /*
    Initial filler of boardArray
     */
    private void fillBoard() {
        //pawns
        for (int i = 0; i < 8; i++) {
            boardArray[1][i].setPiece(new Pawn(Color.Black));
            boardArray[6][i].setPiece(new Pawn(Color.White));
        }

        //rooks
        boardArray[0][0].setPiece(new Rook(Color.Black));
        boardArray[0][7].setPiece(new Rook(Color.Black));
        boardArray[7][0].setPiece(new Rook(Color.White));
        boardArray[7][7].setPiece(new Rook(Color.White));

        //knight
        boardArray[0][1].setPiece(new Knight(Color.Black));
        boardArray[0][6].setPiece(new Knight(Color.Black));
        boardArray[7][1].setPiece(new Knight(Color.White));
        boardArray[7][6].setPiece(new Knight(Color.White));

        //bishop
        boardArray[0][2].setPiece(new Bishop(Color.Black));
        boardArray[0][5].setPiece(new Bishop(Color.Black));
        boardArray[7][2].setPiece(new Bishop(Color.White));
        boardArray[7][5].setPiece(new Bishop(Color.White));

        //queens
        boardArray[0][3].setPiece(new Queen(Color.Black));
        boardArray[7][3].setPiece(new Queen(Color.White));

        //kings
        boardArray[0][4].setPiece(new King(Color.Black));
        boardArray[7][4].setPiece(new King(Color.White));
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
