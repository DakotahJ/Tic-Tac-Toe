package advanced.beginner.TicTacToe;

import java.util.*;

/**
 * Tic Tac Toe abstraction that provide a constructor for creating a 3X3 TicTacToe board
 * in a char[][].
 *
 * Provides methods to make moves, get available moves, number of turns, etc.
 *
 * @author Dakotah Kurtz
 */
public class TicTacToe {

    private final static int HEIGHT = 3;
    private final static int WIDTH = 3;
    public final static char X = 'X';
    public final static char O = 'O';
    public final static char NULL = '-';

    private final char[][] board;
    private int turnCount = 0;
    private final ArrayList<int []> movesAvailable = new ArrayList<>();
    private final ArrayList<ArrayList<int[]>> lines = new ArrayList<>();

    /**
     * Creates a tictactoe board initialized with all null values.
     */

    public TicTacToe(){
        board = new char[WIDTH][HEIGHT];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = NULL;

                movesAvailable.add(new int[]{i, j}); // all spaces are available moves.
            }
        }
        initializeLines();
    }

    public TicTacToe(TicTacToe game) {
        board = new char[WIDTH][HEIGHT];
        turnCount = game.getTurnCount();

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = game.getPieceAt(i, j);

                movesAvailable.add(new int[]{i, j}); // all spaces are available moves.
            }
        }
        initializeLines();
    }

    /**
     * Initializes list of all available rows/columns/diagonals on the board.
     */

    private void initializeLines() {

        // add columns
        for (int i = 0; i < WIDTH; i++) {
            ArrayList<int[]> col = new ArrayList<>();
            for( int j = 0; j < HEIGHT; j++) {
                col.add(new int[]{i, j});
            }
            lines.add(col);
        }

        // add rows
        for (int j = 0; j < HEIGHT; j++) {
            ArrayList<int[]> row = new ArrayList<>();
            for( int i = 0; i < WIDTH; i++) {
                row.add(new int[]{i, j});
            }
            lines.add(row);
        }

        // add diagonals
        ArrayList<int[]> diagonal1 = new ArrayList<>();

        diagonal1.add(new int[]{0,0});
        diagonal1.add(new int[]{1,1});
        diagonal1.add(new int[]{2,2});

        lines.add(diagonal1);

        ArrayList<int[]> diagonal2 = new ArrayList<>();
        diagonal2.add(new int[]{0,2});
        diagonal2.add(new int[]{1,1});
        diagonal2.add(new int[]{2,0});

        lines.add(diagonal2);
    }

    /**
     * Updates board with new move at selected grid location. Automatically places X /
     * O depending on turn count.
     */

    public void move(TicTacToe game, int x, int y, char player) {
        turnCount = game.getTurnCount();

        if (getPieceAt(x, y) != NULL) {
            return;
        }

        else {
            board[x][y] = player;
        }

        turnCount++;
        getMoves(); // update available moves
    }

    public void getNewGame() {

        movesAvailable.clear();
        turnCount = 0;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = NULL;
                movesAvailable.add(new int[]{i, j}); // all spaces are available moves.
            }
        }
    }

    /**
     * Updates arraylist containing all available moves.
     */

    public ArrayList<int[]> getMoves() {

        movesAvailable.clear();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (board[i][j] == NULL) {
                    movesAvailable.add(new int[]{i, j});
                }
            }
        }
        return movesAvailable;
    }

    /**
     * Removes a single move from board.
     */

    public void removeMove(TicTacToe game, int x, int y) {
        board[x][y] = TicTacToe.NULL;
        turnCount--;
    }

    public char getOpponent(char piece) {
        if (piece == TicTacToe.O) {
            return TicTacToe.X;
        }
        else {
            return TicTacToe.O;
        }
    }

    /**
     * Returns list of all lines(rows/columns/diagonals) on the board.
     */

    public ArrayList<ArrayList<int[]>> getLines() {
        return lines;
    }

    /**
     * Returns true if the game is over.
     */

    public boolean gameOver() {
        return (turnCount == HEIGHT * WIDTH) || playerWin() == X || playerWin() == O;
    }

    /**
     * Returns true if the game is a draw.
     */

    public boolean gameDraw() {
        return (turnCount == HEIGHT * WIDTH) && playerWin() != X && playerWin() != O;
    }

    /**
     * Returns turn count.
     */

    public int getTurnCount() {
        return turnCount;
    }

    /**
     * Returns height of game board.
     */

    public int getHeight(){
        return HEIGHT;
    }

    /**
     * Returns width of game board.
     */

    public int getWidth() {
        return WIDTH;
    }

    /**
     * Returns either X or O depending on which player won the game. Returns '-' if no
     * one has yet won.
     */

    public char playerWin() {
        // for each line in the list of all lines

        for (ArrayList<int[]> line : lines) {
            int[] square1 = line.get(0); // get the array at each square
            int[] square2 = line.get(1);
            int[] square3 = line.get(2);

            // get the piece at each square
            char piece1 = getPieceAt(square1[0], square1[1]);
            char piece2 = getPieceAt(square2[0], square2[1]);
            char piece3 = getPieceAt(square3[0], square3[1]);

            // if the pieces are the same (and not all null), return the winning piece.
            if (piece1 == piece2 && piece2 == piece3 && piece1 != TicTacToe.NULL) {
                return piece1;
            }
        }
            return NULL;
    }

    /**
     * Returns X or O depending on whose turn it is
     */

    public char getTurn() {
        if (turnCount % 2 == 0) {
            return TicTacToe.X;
        }
        else {
            return  TicTacToe.O;
        }
    }

    /**
     * Returns piece at given location.
     */

    public char getPieceAt(int x, int y) {
        return board[x][y];
    }

    /**
     * Overrides toString method.
     */

    @Override
    public String toString() {

        String output = "Turn Count: " + turnCount;

        for (int i = 0; i < WIDTH; i++) {
            output += "\n";
            for (int j = 0; j < HEIGHT; j++) {
                output += board[i][j] + " ";
            }
        }
        return output;
    }
}
