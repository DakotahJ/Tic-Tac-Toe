package advanced.beginner.TicTacToe;


import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class that represents TicTacToeAI. Provides methods that give different types of move
 * selection algorithms.
 *
 * @author Dakotah Kurtz
 */

public class TicTacToeAI {

    private ArrayList<int[]> movesAvailable;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final TicTacToe game;

    /**
     * Constructor to take information from TicTacToe game.
     */
    public TicTacToeAI(TicTacToe game) {
        this.game = game;
        movesAvailable = game.getMoves();
    }

    /**
     * Returns an int[] with grid location of a random legal move.
     */
    public int[] getRandomMove() {

            movesAvailable = game.getMoves();
            int numOfMoves = movesAvailable.size();
            // Pick a random legal move from list of legal moves.
            int choice = random.nextInt(numOfMoves);

            return movesAvailable.get(choice);
    }

    /**
     * Returns the winning move as an int[] if winning move exists. If not, returns a 
     * random move.
     */

    public int[] getWinningMove(char player) {
        // all valid combinations of three in a row
        ArrayList<ArrayList<int[]>> lines = game.getLines();

        // in each line, track how many pieces the player has and how many are open
        for (ArrayList<int[]> line : lines) {
            int playerCount = 0;
            int open = 0;
            int[] move = new int[2];

            for (int[] square : line) {
                char piece = game.getPieceAt(square[0], square[1]);

                if (piece == player) {
                    playerCount++;
                } else if (piece == TicTacToe.NULL) {
                    open++;
                    move = new int[]{square[0], square[1]};
                }
                // if player has two pieces in a row and the third is open, return that
                // move.
                if (playerCount == 2 && open == 1) {
                    return move;
                }
            }
        }
        // otherwise return random move.
        return getRandomMove();
    }

    /**
     * Returns winning move if available. If not, attempts to block opponent from getting
     * three in a row. If neither situation is possible, returns a random move.
     */

    public int[] getWinBlockLoseAI(char player) {
        // all combinations of three in a row
        ArrayList<ArrayList<int[]>> lines = game.getLines();
        char opposing = game.getOpponent(player);
        // in each row, tracks player pieces, opponent pieces, and empty spaces
        for (ArrayList<int[]> line : lines) {
            int playerCount = 0;
            int opposingCount = 0;
            int open = 0;
            int[] move = new int[2];

            for (int[] square : line) {
                char piece = game.getPieceAt(square[0], square[1]);

                if (piece == player) {
                    playerCount++;
                } else if (piece == opposing) {
                    opposingCount++;
                } else if (piece == TicTacToe.NULL) {
                    open++;
                    move = new int[]{square[0], square[1]};
                }
                // if a winning move exists
                if (playerCount == 2 && open == 1) {
                    return move;
                    // if a block is possible
                } else if (opposingCount == 2 && open == 1) {
                    return move;
                }
            }
        }
        // otherwise return a random move.
        return getRandomMove();
    }

    /**
     * This method is meant to utilize the minimax algorithm to find the optimal move 
     * (assuming opponent is playing optimally). Does not consider number of moves to 
     * outcome as a factor.
     *
     * @param player - the player to move
     * @return int[2] containing the (x,y) coordinates of the optimal move
     */

    public int[] getBestMove(char player) {
        int[] bestMove = new int[]{-1, 1};
        int bestScore = Integer.MIN_VALUE;

        // For each move in the list of available moves
        for (int i = 0; i < movesAvailable.size(); i++) {
            TicTacToe newGame = new TicTacToe(game); // create a new game
            int[] move = movesAvailable.get(i);
            newGame.move(newGame, move[0], move[1], player); // place move on new board

            char opp = game.getOpponent(player); // returns X / O
            int score = minimaxScore(newGame, opp, player); // Run minimax algo on each
            // move.

            // save current best score
            if (bestScore < score) {
                bestMove = move;
                bestScore = score;
            }
        }
        return bestMove;
    }

    /**
     * Minimax algorithm. Recursively moves down the tree through all possible game-states
     * following a given move. Returns a score that assumes all players play optimally.
     * Credit to CodeTrain on Youtube for providing a template.
     */

    public int minimaxScore(TicTacToe game, char playerToMove, char playerToOptimize) {
        // returns the winner of the game or NULL if no one has won yet.
        char winner = game.playerWin();

        if (game.gameOver()) {
            if (winner == playerToOptimize) {
                return 10;
            }
            else if (game.gameDraw()) {
                return 0;
            }
            else {
                return -10;
            }
        }

        // gets updated list of all remaining legal moves
        ArrayList<int[]> legalMoves = game.getMoves();

        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;

        // for each legal move left
        for (int i = 0; i < legalMoves.size(); i++) {
            TicTacToe newGame = new TicTacToe(game);
            int[] move = legalMoves.get(i);
            // try move
            newGame.move(newGame, move[0], move[1], playerToMove);

            // should be alternating between X and O
            char opp = newGame.getOpponent(playerToMove);
            // check each game state down the tree
            int oppBestResponseScore = minimaxScore(newGame, opp, playerToOptimize);
            // keep track of highest and lowest score
            maxScore = Math.max(oppBestResponseScore, maxScore);
            minScore = Math.min(oppBestResponseScore, minScore);
        }

        // AI wants high score, other player will play for lowest score
        if (playerToMove == playerToOptimize) {
            return maxScore;
        }
        else {
            return minScore;
        }
    }

}


