package waterfall.game.chess;

import waterfall.game.Board;
import waterfall.game.Coordinates;
import waterfall.game.chess.pieces.*;

import java.util.ArrayList;

public class ChessBoard implements Board {
    private final Tile[][] board;

    public ChessBoard() {
        board = new Tile[8][8];
        initializeBoard();
        fillBoard();
    }

    public Tile[][] getBoardArray() {
        return board;
    }

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (j % 2 + i == 0) board[i][j] = new Tile(Tile.TileColor.Black);
                else board[i][j] = new Tile(Tile.TileColor.White);
            }
        }
    }

    //Will break on boards with no Kings of 'color'. Should never happen.
    public Coordinates getKingLocation(ChessPiece.PieceColor color) {
        Coordinates location = new Coordinates(-1, -1);
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (!board[y][x].isEmpty()) {
                    ChessPiece piece = board[y][x].getPiece();
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
                if (!board[y][x].isEmpty() && board[y][x].getPiece().getColor() == color)
                    locations.add(new Coordinates(x, y));
            }
        }
        return locations.toArray(new Coordinates[0]);//allocate new array automatically.
    }

    public Tile getTileFromCoordinates(Coordinates coordinates) {
        return board[coordinates.getY()][coordinates.getX()];
    }

    /*
    Initial filler of board
     */
    private void fillBoard() {
        //pawns
        for (int i = 0; i < 8; i++) {
            board[1][i].setPiece(new Pawn(ChessPiece.PieceColor.Black));
            board[6][i].setPiece(new Pawn(ChessPiece.PieceColor.White));
        }

        //rooks
        board[0][0].setPiece(new Rook(ChessPiece.PieceColor.Black));
        board[0][7].setPiece(new Rook(ChessPiece.PieceColor.Black));
        board[7][0].setPiece(new Rook(ChessPiece.PieceColor.White));
        board[7][7].setPiece(new Rook(ChessPiece.PieceColor.White));

        //knight
        board[0][1].setPiece(new Knight(ChessPiece.PieceColor.Black));
        board[0][6].setPiece(new Knight(ChessPiece.PieceColor.Black));
        board[7][1].setPiece(new Knight(ChessPiece.PieceColor.White));
        board[7][6].setPiece(new Knight(ChessPiece.PieceColor.White));

        //bishop
        board[0][2].setPiece(new Bishop(ChessPiece.PieceColor.Black));
        board[0][5].setPiece(new Bishop(ChessPiece.PieceColor.Black));
        board[7][2].setPiece(new Bishop(ChessPiece.PieceColor.White));
        board[7][5].setPiece(new Bishop(ChessPiece.PieceColor.White));

        //queens
        board[0][3].setPiece(new Queen(ChessPiece.PieceColor.Black));
        board[7][3].setPiece(new Queen(ChessPiece.PieceColor.White));

        //kings
        board[0][4].setPiece(new King(ChessPiece.PieceColor.Black));
        board[7][4].setPiece(new King(ChessPiece.PieceColor.White));
    }
}
