package waterfall.game.chess;

import waterfall.game.*;
import waterfall.game.chess.ChessPiece.PieceColor;
import waterfall.game.chess.ChessPiece.PieceType;

import java.util.ArrayList;
import java.util.List;

public class ChessGame implements Game {

    private final ChessBoard board;
    private boolean isFinished;
    private PieceColor currentPlayer;
    private Player player;
    private Player playerWinner;
    private List<Player> playerList;

    public ChessGame() {
        board = new ChessBoard();
        currentPlayer = PieceColor.White;
        playerList = new ArrayList<Player>(2);
        isFinished = false;
    }

    /**
     * @return returns true if moveRule was played, false if moveRule was illegal
     */
    @Override
    public String playMove(Move move, Player player) {
        String message = isValidMove(move.getStart(), move.getDestination(), player, false);

        Tile fromTile = (Tile) board.getBoardArray()[move.getDestination().getY()][move.getDestination().getX()];
        ChessPiece pieceToMove = fromTile.getPiece();

        Tile toTile = (Tile) board.getBoardArray()[move.getDestination().getY()][move.getDestination().getX()];
        toTile.setPiece(pieceToMove);

        fromTile.empty();
        endTurn();

        return message;
    }

    @Override
    public boolean registerPlayer(Player player) {
        player.setMark(getMark());

        return playerList.add(player);
    }

    @Override
    public Move convertToMove(String coordsMove) {
        char alphaCoordinates[] = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        String[] coordinates = coordsMove.split("\\s+");

        int firstCoord = 0;
        for (int i = 0; i < alphaCoordinates.length; i++) {
            if (alphaCoordinates[i] == coordinates[0].charAt(0)) {
                firstCoord = i;
            }
        }

        int secondCoord = 0;
        for (int i = 0; i < alphaCoordinates.length; i++) {
            if (alphaCoordinates[i] == coordinates[1].charAt(0)) {
                secondCoord = i;
            }
        }

        return new Move(new Coordinates(firstCoord, Integer.valueOf(coordinates[0].charAt(1))),
                new Coordinates(secondCoord, Integer.valueOf(coordinates[1].charAt(1))));
    }

    /**
     * @return returns the current ChessBoard associated with the game.
     */
    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public Player getWinner() {
        return playerWinner;
    }

    /**
     * @return returns whether the given ChessGame is finished.
     */
    @Override
    public boolean isFinished() {
        return isFinished;
    }

    public Player getOpponent(Player player) {
        if (playerList.get(0).equals(player)) {
            return playerList.get(1);
        } else {
            return playerList.get(0);
        }
    }

    public String getMark() {
        if (getPlayer().equals(PieceColor.White.name())) {
            return PieceColor.Black.name();
        } else {
            return PieceColor.White.name();
        }
    }

    private Player getPlayer() {
        if (playerList.get(0) != null) {
            return playerList.get(0);
        } else {
            return playerList.get(1);
        }
    }

    private void endTurn() {
        currentPlayer = (currentPlayer == PieceColor.White)
                ? PieceColor.Black
                : PieceColor.White;

        player = getOpponent(player);
    }

    // Function that checks if any piece can prevent check for the given color
    // This includes whether the King can move out of check himself.
    private boolean isCheckPreventable(PieceColor color) {
        boolean canPreventCheck = false;
        Coordinates[] locations = board.getAllPiecesLocationForColor(color);

        for (Coordinates location : locations) {
            Tile fromTile = board.getTileFromCoordinates(location);
            ChessPiece piece = fromTile.getPiece();
            Coordinates[] possibleMoves = validMovesForPiece(piece, location);

            for (Coordinates newLocation : possibleMoves) {
                Tile toTile = board.getTileFromCoordinates(newLocation);
                ChessPiece toPiece = toTile.getPiece();

                //temporarily play the move to see if it makes us check
                toTile.setPiece(piece);
                fromTile.empty();

                //if we're no longer check
                if (!isKingCheck(color)) {
                    canPreventCheck = true;
                }

                //revert temporary move
                toTile.setPiece(toPiece);
                fromTile.setPiece(piece);
                if (canPreventCheck) { // early out
                    System.out.printf("Prevented with from:" + fromTile + ", to: " + toTile);
                    return canPreventCheck;
                }
            }
        }
        return canPreventCheck;
    }

    private boolean isColorCheckMate(PieceColor color) {
        if (!isKingCheck(color)) return false;//if not check, then we're not mate
        return !isCheckPreventable(color);
    }

    private boolean isKingCheck(PieceColor kingColor) {
        Coordinates kingLocation = board.getKingLocation(kingColor);
        return canOpponentTakeLocation(kingLocation, kingColor);
    }

    private boolean canOpponentTakeLocation(Coordinates location, PieceColor color) {
        PieceColor opponentColor = ChessPiece.opponent(color);
        Coordinates[] piecesLocation = board.getAllPiecesLocationForColor(opponentColor);

        for (Coordinates fromCoordinates : piecesLocation) {
            if (isValidMove(fromCoordinates, location, null, true).equals("hypothetical"))
                return true;
        }
        return false;
    }

    /**
     * @param from         the position from which the player tries to move from
     * @param to           the position the player tries to move to
     * @param hypothetical if the move is hypothetical, we disregard if it sets the from player check
     * @return a boolean indicating whether the move is valid or not
     */
    private String isValidMove(Coordinates from, Coordinates to, Player player, boolean hypothetical) {
        if (player != this.player && player != null) {
            return "Not your turn";
        }

        Tile fromTile = board.getTileFromCoordinates(from);
        Tile toTile = board.getTileFromCoordinates(to);
        ChessPiece fromPiece = fromTile.getPiece();
        ChessPiece toPiece = toTile.getPiece();

        if (fromPiece == null) {
            return "Piece is not chosen";
        } else if (fromPiece.getColor() != currentPlayer) {
            return "Not your piece";
        } else if (toPiece != null && toPiece.getColor() == currentPlayer) {
            return "The tile is blocked with your piece";
        } else if (isValidMoveForPiece(from, to)) {
            //if hypothetical and valid, return true
            if (hypothetical) return "hypothetical";

            //temporarily play the move to see if it makes us check
            toTile.setPiece(fromPiece);
            fromTile.empty();
            if (isKingCheck(currentPlayer)) {//check that move doesn't put oneself in check
                toTile.setPiece(toPiece);
                fromTile.setPiece(fromPiece);
                return "Can't move there. King check";
            }

            //if mate, finish game
            if (isColorCheckMate(ChessPiece.opponent(currentPlayer)))
                isFinished = true;

            //revert temporary move
            toTile.setPiece(toPiece);
            fromTile.setPiece(fromPiece);

            return "Moved from " + from + " to " + to;
        }
        return "Invalid move";
    }

    // A utility function that gets all the possible moves for a piece, with illegal ones removed.
    // NOTICE: Does not check for counter-check when evaluating legality.
    //         This means it mostly checks if it is a legal move for the piece in terms
    //         of ensuring its not taking one of its own, and within its 'possibleMoves'.
    // Returns the Coordinatess representing the Tiles to which the given piece
    // can legally move.
    private Coordinates[] validMovesForPiece(ChessPiece piece, Coordinates currentLocation) {
        return piece.hasRepeatableMoves()
                ? validMovesRepeatable(piece, currentLocation)
                : validMovesNonRepeatable(piece, currentLocation);
    }

    // Returns the Coordinatess representing the Tiles to which the given piece
    // can legally move.
    private Coordinates[] validMovesRepeatable(ChessPiece piece, Coordinates currentLocation) {
        MoveRule[] moves = piece.getMoveRules();
        ArrayList<Coordinates> possibleMoves = new ArrayList<>();

        for (MoveRule moveRule : moves) {
            for (int i = 1; i < 7; i++) {
                int newX = currentLocation.getX() + moveRule.x * i;
                int newY = currentLocation.getY() + moveRule.y * i;
                if (newX < 0 || newX > 7 || newY < 0 || newY > 7) break;

                Coordinates toLocation = new Coordinates(newX, newY);
                Tile tile = board.getTileFromCoordinates(toLocation);
                if (tile.isEmpty()) {
                    possibleMoves.add(toLocation);
                } else {
                    if (tile.getPiece().getColor() != piece.getColor())
                        possibleMoves.add(toLocation);
                    break;
                }
            }
        }
        return possibleMoves.toArray(new Coordinates[0]);
    }

    private Coordinates[] validMovesNonRepeatable(ChessPiece piece, Coordinates currentLocation) {
        MoveRule[] moveRules = piece.getMoveRules();
        ArrayList<Coordinates> possibleMoves = new ArrayList<>();

        for (MoveRule moveRule : moveRules) {
            int currentX = currentLocation.getX();
            int currentY = currentLocation.getY();
            int newX = currentX + moveRule.x;
            int newY = currentY + moveRule.y;
            if (newX < 0 || newX > 7 || newY < 0 || newY > 7) continue;
            Coordinates newLocation = new Coordinates(newX, newY);
            if (isValidMoveForPiece(currentLocation, newLocation)) possibleMoves.add(newLocation);
        }
        return possibleMoves.toArray(new Coordinates[0]);
    }

    // Checks whether a given move from from one coordinates to another is valid.
    private boolean isValidMoveForPiece(Coordinates from, Coordinates to) {
        ChessPiece fromPiece = board.getTileFromCoordinates(from).getPiece();
        boolean repeatableMoves = fromPiece.hasRepeatableMoves();

        return repeatableMoves
                ? isValidMoveForPieceRepeatable(from, to)
                : isValidMoveForPieceNonRepeatable(from, to);
    }

    // Check whether a given move is valid for a piece without repeatable moves.
    private boolean isValidMoveForPieceRepeatable(Coordinates from, Coordinates to) {
        ChessPiece fromPiece = board.getTileFromCoordinates(from).getPiece();
        MoveRule[] validMoveRules = fromPiece.getMoveRules();

        int xMove = to.getX() - from.getX();
        int yMove = to.getY() - from.getY();

        for (int i = 1; i <= 7; i++) {
            for (MoveRule moveRule : validMoveRules) {

                //generally check for possible moveRule
                if (moveRule.x * i == xMove && moveRule.y * i == yMove) {

                    //if moveRule is generally valid - check if path is valid up till i
                    for (int j = 1; j <= i; j++) {
                        Tile tile = board.getTileFromCoordinates(new Coordinates(from.getX() + moveRule.x * j, from.getY() + moveRule.y * j));
                        //if passing through non empty tile return false
                        if (j != i && !tile.isEmpty())
                            return false;

                        //if last moveRule and toTile is empty or holds opponents piece, return true
                        if (j == i && (tile.isEmpty() || tile.getPiece().getColor() != currentPlayer))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    // Check whether a given move is valid for a piece with repeatable moves.
    private boolean isValidMoveForPieceNonRepeatable(Coordinates from, Coordinates to) {
        ChessPiece fromPiece = board.getTileFromCoordinates(from).getPiece();
        MoveRule[] validMoveRules = fromPiece.getMoveRules();
        Tile toTile = board.getTileFromCoordinates(to);

        int xMove = to.getX() - from.getX();
        int yMove = to.getY() - from.getY();

        for (MoveRule moveRule : validMoveRules) {
            if (moveRule.x == xMove && moveRule.y == yMove) {
                if (moveRule.onTakeOnly) {//if moveRule is only legal on take (pawns)
                    if (toTile.isEmpty()) return false;

                    ChessPiece toPiece = toTile.getPiece();
                    return fromPiece.getColor() != toPiece.getColor();//if different color, valid moveRule

                    //handling first moveRule only for pawns - they should not have moved yet
                } else if (moveRule.firstMoveOnly) {
                    return toTile.isEmpty() && isFirstMoveForPawn(from, board);
                } else {
                    return toTile.isEmpty();
                }
            }
        }
        return false;
    }

    // Determine wheter the Pawn at 'from' on 'board' has moved yet.
    public boolean isFirstMoveForPawn(Coordinates from, ChessBoard board) {
        Tile tile = board.getTileFromCoordinates(from);
        if (tile.isEmpty() || tile.getPiece().getPieceType() != PieceType.Pawn) {
            return false;
        } else {
            PieceColor color = tile.getPiece().getColor();
            return (color == PieceColor.White)
                    ? from.getY() == 6
                    : from.getY() == 1;
        }
    }
}
