package waterfall.game.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import waterfall.game.Board;
import waterfall.game.Color;
import waterfall.game.Coordinates;
import waterfall.game.Game;
import waterfall.game.Move;
import waterfall.game.Player;
import waterfall.game.Tile;


public class MinimaxAlphaBeta {

    Color color;
    Player player;
    int maxDepth;
    Random rand;

    public MinimaxAlphaBeta(Player player, int maxDepth) {
        this.color = getColorFromMark(player.getMark());
        this.player = player;
        this.maxDepth = maxDepth;
        rand = new Random();
    }

    private float maxValue(ChessGame game, ArrayList<Move> state, float alpha, float beta, int depth) {
        if(depth > maxDepth)
            return eval1(game, state, color);

        List<Move> moves = game.getMovesAfter(color, state);
        if(moves.size() == 0) // TODO add draw
            return Float.NEGATIVE_INFINITY;

        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = minValue(game, state, alpha, beta, depth + 1);
            state.remove(state.lastIndexOf(moves.get(i)));
            if(tmp > alpha) {
                alpha = tmp;
            }

            if(beta <= alpha)
                break;

            //if (max >= beta)
            //	return max;

            //if (max > alpha)
            //	alpha = max;
        }

        return alpha;
    }

    private float minValue(ChessGame game, ArrayList<Move> state, float alpha, float beta, int depth) {
        if(depth > maxDepth)
            return eval1(game, state, getOppositeColor(color));

        List<Move> moves = game.getMovesAfter(getOppositeColor(color), state);
        if(moves.size() == 0) // TODO add draw
            return Float.POSITIVE_INFINITY;

        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = maxValue(game, state, alpha, beta, depth + 1);
            state.remove(state.lastIndexOf(moves.get(i)));
            if(tmp < beta) {
                beta = tmp;
            }

            if(beta <= alpha)
                break;


            //if (min <= beta)
            //	return min;

            //if (min < beta)
            //	beta = min;
        }

        return beta;
    }

    public Move decision(Game game) {
        // get maximum move

        List<Move> moves = getMoves((ChessGame)game, color);
        if(moves.size() == 0)
            return null;

        Vector<Future<Float>> costs = new Vector<Future<Float>>(moves.size());
        costs.setSize(moves.size());

        ExecutorService exec = Executors.newFixedThreadPool(moves.size());
        try {
            for (int i = 0; i < moves.size(); i++) {
                final Move move = moves.get(i);
                Future<Float> result = exec.submit(new Callable<Float>() {

                    @Override
                    public Float call() {
                        ArrayList<Move> state = new ArrayList<Move>();
                        state.add(move);

                        float tmp = minValue((ChessGame) game, state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
                        return Float.valueOf(tmp);
                    }
                });
                costs.set(i, result);
            }
        } finally {
            exec.shutdown();
        }

        // max
        int maxi = -1;
        float max = Float.NEGATIVE_INFINITY;
        for(int i = 0; i < moves.size(); i++) {
            float cost;
            try {
                cost = costs.get(i).get().floatValue();
            } catch (Exception e) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e1) {
                }
                continue;
            }
            if(cost >= max) {
                if(Math.abs(cost-max) < 0.1) // add a little random element
                    if(rand.nextBoolean())
                        continue;

                max = cost;
                maxi = i;
            }
        }

        return moves.get(maxi);
    }

    public Move SingleThreadDecision(ChessGame game) {
        // get maximum move

        List<Move> moves = getMoves(game, color);
        ArrayList<Move> state = new ArrayList<Move>();
        float[] costs = new float[moves.size()];
        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = minValue(game, state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
            costs[i] = tmp;
            state.remove(state.lastIndexOf(moves.get(i)));
        }

        // max
        int maxi = -1;
        float max = Float.NEGATIVE_INFINITY;
        for(int i = 0; i < moves.size(); i++) {
            if(costs[i] >= max) {
                if(Math.abs(costs[i]-max) < 0.1) // add a little random element
                    if(rand.nextBoolean())
                        continue;

                max = costs[i];
                maxi = i;
            }
        }

        if(maxi == -1)
            return null;
        else
            return moves.get(maxi);
    }

    private float eval1(ChessGame game, ArrayList<Move> moves, Color currentColor) {
        Tile[][] tiles = game.getTilesAfter(moves);

        if(getMoves(game, color).size() == 0) {
            if(game.isCheckAfter(currentColor, moves))
                return (currentColor == this.color) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            else
                return Float.NEGATIVE_INFINITY; // we don't like draws
        }

        int whiteScore = 0;
        int blackScore = 0;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j].getPiece() != null)
                    if(tiles[i][j].getPiece().getColor() == Color.White)
                        whiteScore += tiles[i][j].getPiece().getCost();
                    else
                        blackScore += tiles[i][j].getPiece().getCost();
            }


        if(color == Color.White)
            return whiteScore - blackScore;
        else
            return blackScore - whiteScore;
    }

    private Color getOppositeColor(Color color) {
        return color.equals(Color.White) ? Color.Black : Color.White;
    }

    private Color getColorFromMark(String mark) {
        return mark.equals("Black") ? Color.Black : Color.White;
    }

    private List<Move> getMoves(ChessGame game, Color color) {
        Board board = game.getBoard();
        List<Move> possibleMoves = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                ChessPiece piece = (ChessPiece) board.getBoardArray()[i][j].getPiece();
                if(piece != null && piece.getColor().equals(color)) {
                    for(MoveRule rule: piece.getMoveRules()) {
                        if(game.isValidMove(new Coordinates(i, j), new Coordinates(rule.x, rule.y), player, false).equals("Valid move")) {
                            possibleMoves.add(new Move(new Coordinates(i,j), new Coordinates(rule.x, rule.y)));
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }
}
