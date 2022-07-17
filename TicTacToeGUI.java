package advanced.beginner.TicTacToe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Displays a Tic-Tac-Toe game with mouse interface. Click on board to begin playing.
 * Game begins on difficulty "2" but can be adjusted during or between games. Allows
 * human vs human gameplay. Everyone Is A Winner game mode, inspired by
 * rrrrthats5rs.com, has a different goal: try to force the game into a draw.
 *
 * @author Dakotah Kurtz
 */

public class TicTacToeGUI extends Application {

    private final TicTacToe game = new TicTacToe();

    private final double height = 500;
    private final double width = 500;
    private final double gap = height * .05;
    private final Color backgroundColor = Color.ANTIQUEWHITE;
    private char whichPlayer = TicTacToe.X;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        // Creates and formats board where gameplay takes place.
        Pane boardLayout = new Pane();
        boardLayout.setMaxSize(width, height);
        boardLayout.setPrefSize(width, height);
        drawGrid(boardLayout);

        BorderPane gameOptionFooter = new BorderPane();
        gameOptionFooter.setPadding(new Insets(5));

        double buttonSizeInc = (height + width) / 100;

        // Creates button to start a new game.
        Button newGameButton = new Button("New Game");
        newGameButton.setPrefWidth(buttonSizeInc * 10);
        newGameButton.setPrefHeight(buttonSizeInc * 8);

        Tooltip tooltip = new Tooltip("Warning: Will start a new game");

        // Creates toggle buttons to switch between human and AI.
        ToggleButton humanButton = new ToggleButton("Human");
        humanButton.setPrefWidth(buttonSizeInc * 10);
        humanButton.setPrefHeight(buttonSizeInc * 5);
        humanButton.setTooltip(tooltip);

        ToggleButton AIButton = new ToggleButton("AI");
        AIButton.setPrefWidth(buttonSizeInc * 10);
        AIButton.setPrefHeight(buttonSizeInc * 5);
        AIButton.setSelected(true);
        AIButton.setTooltip(tooltip);

        Slider difficultySlider = new Slider(0, 3, 2);
        difficultySlider.setBlockIncrement(1);
        difficultySlider.setMajorTickUnit(1);
        difficultySlider.setMinorTickCount(0);
        difficultySlider.setShowTickLabels(true);
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setTooltip(new Tooltip("Difficulty"));

        // Adds human and AI ToggleButtons to GridPane.
        GridPane humanAiGridPane = new GridPane();
        humanAiGridPane.add(humanButton, 0, 0);
        humanAiGridPane.add(AIButton, 0, 1);
        humanAiGridPane.add(difficultySlider, 0, 2);

        // Creates ToggleButtons to switch between game modes.
        ToggleButton standardGameModeButton = new ToggleButton("Standard");
        standardGameModeButton.setPrefWidth(100);
        standardGameModeButton.setPrefHeight(50);
        standardGameModeButton.setSelected(true);
        standardGameModeButton.setTooltip(tooltip);

        ToggleButton EAWGameModeButton = new ToggleButton("Everyone's a Winner!");
        EAWGameModeButton.setPrefWidth(150);
        EAWGameModeButton.setPrefHeight(50);
        EAWGameModeButton.setTooltip(tooltip);

        // Adds game mode ToggleButtons to GridPane.
        GridPane gameModeGridPane = new GridPane();
        gameModeGridPane.setAlignment(Pos.CENTER);
        gameModeGridPane.add(standardGameModeButton, 0, 0);
        gameModeGridPane.add(EAWGameModeButton, 0, 1);

        // Set buttons and ToggleButton GridPanes to footer.
        gameOptionFooter.setLeft(newGameButton);
        gameOptionFooter.setRight(humanAiGridPane);
        gameOptionFooter.setCenter(gameModeGridPane);

        root.setCenter(boardLayout);
        root.setBottom(gameOptionFooter);

        // Creates background of GUI.
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor,
                CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        root.setBackground(background);

        // Creates the stackpanes which control the game logic and hold X's / O's.
        StackPane[][] stackPanes = new StackPane[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    stackPanes[i][j] = new StackPane();
                    formatStackPane(stackPanes[i][j], j, i);
                    boardLayout.getChildren().add(stackPanes[i][j]);

                    int finalJ = j;
                    int finalI = i;

                    /*
                     * For each stackpane, set a mouseclick event that updates TicTacToe
                     * object according to the square clicked.
                     */

                    stackPanes[i][j].setOnMouseClicked(event -> {

                        // While the game isn't over, update gameboard upon click.
                        if (!game.gameOver()) {

                            game.move(game, finalI, finalJ, whichPlayer);
                            drawPieces(stackPanes);

                            // if the AIButton is selected, get opposing move from the AI.
                            if (AIButton.isSelected() && !game.gameOver()) {

                                int[] nextMove =
                                        getAIMove((int) difficultySlider.getValue(),
                                        TicTacToe.O);
                                game.move(game, nextMove[0], nextMove[1], TicTacToe.O);
                                drawPieces(stackPanes);
                            }

                            // if AI button is not selected, switch control to next
                            // player.
                            if (!AIButton.isSelected()) {
                                whichPlayer = game.getOpponent(whichPlayer);
                            }
                        }
                        // If the game is over, display message
                        if (game.gameOver()) {
                            endGame(primaryStage, EAWGameModeButton.isSelected());
                        }
                    });
                }
            }

        humanButton.setOnMouseClicked(event -> {
            AIButton.setSelected(false);
            difficultySlider.setDisable(true);
            getNewGame(stackPanes);
        });

        AIButton.setOnMouseClicked(event -> {
            humanButton.setSelected(false);
            difficultySlider.setDisable(false);
            getNewGame(stackPanes);
        });

        EAWGameModeButton.setOnMouseClicked(event -> {
            standardGameModeButton.setSelected(false);
            difficultySlider.setValue(0); // only one difficulty in this game mode.
            difficultySlider.setDisable(true);
            getNewGame(stackPanes);
        });

        standardGameModeButton.setOnMouseClicked(event -> {
            EAWGameModeButton.setSelected(false);
            difficultySlider.setValue(2);
            difficultySlider.setDisable(false);
            getNewGame(stackPanes);
        });

        newGameButton.setOnMouseClicked(event -> {
                getNewGame(stackPanes); // Remove X's/O's from GUI
            });

        Scene scene = new Scene(root, backgroundColor);
        primaryStage.setScene(scene);

        primaryStage.setTitle("TicTacToe");
        primaryStage.show();
    }

    /**
     * Returns the next move generated by the computer as an int[], selected based off
     * of the games current difficulty.
     */

    private int[] getAIMove(int difficulty, char player) {
        TicTacToeAI ai = new TicTacToeAI(game);
        int[] move = new int[2];

        switch (difficulty) {
            case 0: move = ai.getRandomMove();
            break;
            case 1: move = ai.getWinningMove(player);
            break;
            case 2: move = ai.getWinBlockLoseAI(player);
            break;
            case 3: move = ai.getBestMove(player);
        }
        return move;
    }

    /**
     * Clears the board of all X's and O's
     */

    private void getNewGame(StackPane[][] stackPanes) {

        game.getNewGame(); // reset game engine

        // clear the board
        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {

                stackPanes[i][j].getChildren().clear();
                formatStackPane(stackPanes[i][j], j, i);
            }
        }
    }

    /**
     * Draw X's and O's in appropriate StackPanes according to game board state.
     *
     * COULD BE OPTIMIZED
     */
    private void drawPieces(StackPane[][] stackPanes) {

        for (int i = 0; i < game.getHeight(); i++) {
            for (int j = 0; j < game.getWidth(); j++) {

                if (game.getPieceAt(i, j) == TicTacToe.X) {
                    drawX(stackPanes[i][j]);

                } else if (game.getPieceAt(i, j) == TicTacToe.O) {
                    drawCircle(stackPanes[i][j]);

                }
            }
        }
    }

    /**
     * Display alert message indicating the game is over and the game's outcome
     */

    public void endGame(Stage stage, boolean everyonesAWinner) {

        stage.setAlwaysOnTop(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GAME OVER");
        String headerText = "";

        // Determine alert message based on game board state and game mode.

        if (everyonesAWinner) { // if everyone is a winner game mode is selected
            if (game.gameDraw()) {
                headerText += "Everyone Wins!";
            }
            else {
                headerText += "Why is it always about winning?";
            }
        }

        else {
            if (game.gameDraw()) {
                headerText += "The game is a draw";
            } else if (game.playerWin() == TicTacToe.X) {
                headerText += "X wins!";
            } else if (game.playerWin() == TicTacToe.O) {
                headerText += "O wins!";
            }
        }

        alert.setHeaderText(headerText);
        alert.setContentText("Click New Game to Play Again");
        alert.showAndWait();
    }

    /**
     * Draws the game grid.
     */
    public void drawGrid(Pane root) {

        double divisionX = width / 3;
        double divisionY = height / 3;

        // Vertical lines spaced at 1/3 and 2/3 of width
        Line verticalLeft = new Line(divisionX, gap, divisionX, height - gap );
        Line verticalRight = new Line(divisionX * 2, gap, divisionX * 2,
                height - gap);
        // Horizontal lines spaced at 1/3 and 2/3 of height
        Line horizontalUpper = new Line(gap, divisionY, width - gap, divisionY);
        Line horizontalLower = new Line( gap, divisionY * 2, width - gap,
                divisionY * 2);

        formatLine(verticalLeft);
        formatLine(verticalRight);
        formatLine(horizontalUpper);
        formatLine(horizontalLower);

        root.getChildren().addAll(verticalLeft, verticalRight, horizontalUpper,
                horizontalLower);
    }

    /**
     * Provides standardized circle to be drawn (representing an O)
     */

    public void drawCircle(StackPane stackPane) {
        Circle circle = new Circle(((width / 3) - 2 * gap) / 2, backgroundColor);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(8);
        stackPane.getChildren().add(circle);
    }

    /**
     * Provides standardized X to be drawn.
     */

    public void drawX(StackPane stackPane) {
        Text text = new Text("X");
        double textHeight = ((height + width) / 100) * 13;
        String style = "-fx-font-size: " + textHeight + ";";
        text.setStyle(style);
        stackPane.getChildren().add(text);
    }

    /**
     * Formats a StackPane to appropriate size (1/3 width and 1/3 height) and adds a
     * square to distinguish location of users mouse click.
     */

    public void formatStackPane(StackPane stackPane, int x, int y) {
        int spacer = 5;
        double divisionX = width / 3;
        stackPane.setLayoutX((x * divisionX) + spacer);
        double divisionY = height / 3;
        stackPane.setLayoutY((y * divisionY) + spacer);

        // adds rectangle to precisely bound area of valid user clicks on game grid.
        stackPane.getChildren().add(new Rectangle(divisionX - spacer * 2,
                divisionY - spacer * 2,
                backgroundColor));
    }

    /**
     * Format the lines that make up a Tic Tac Toe grid.
     */

    public void formatLine(Line line) {
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(5);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
