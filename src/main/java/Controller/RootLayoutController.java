package main.java.Controller;

import main.java.Others.Board;
import main.java.Others.GameManager;
import main.java.Main;
import main.java.Others.Position;
import main.java.Utils.Colour;
import main.java.Utils.GameMode;
import main.java.Utils.GamePhase;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Hee Zhan Zhynn
 * <p>
 * This class is the controller for the root layout. It handles the drag and drop functionality of the game.
 * <p>
 * The drag and drop functionality is implemented using the JavaFX Drag and Drop API.
 * <p>
 * Reference:
 * https://www.javatpoint.com/javafx-convenience-methods
 * https://github.com/OmDharme/Chess---JavaFX
 * https://github.com/zann1x/MerelsFX*
 */

public class RootLayoutController {


    private Stage stage;
    private Board board;
    private GameManager gameManager;

    private GameMode rootGameMode;

    private ObservableList<ImageView> boardGridChildren = FXCollections.observableArrayList();

    private ObservableList<ImageView> rightPocketGridChildren = FXCollections.observableArrayList();

    @FXML
    private GridPane gameBoardGrid;     // game board grid is the main game board to place token on
    @FXML
    private GridPane leftPocketGrid;    // pocket grid is initial token placement before game starts
    @FXML
    private GridPane rightPocketGrid;
    @FXML
    private Label playerTurnLabel;      // label to display current player turn
    @FXML
    private MenuItem musicLabel;
    private main.java.Controller.SceneController sceneController;    //to handle exit to main menu from game scene

    /**
     * Returns the Others.Position of the image view in the corresponding GridPane.
     *
     * @param iv image view to be checked
     * @return Others.Position of the image view
     */
    private Position getTilePosition(ImageView iv) {
        Integer column = GridPane.getColumnIndex(iv);
        Integer row = GridPane.getRowIndex(iv);
        return new Position(column == null ? 0 : column, row == null ? 0 : row);
    }

    /**
     * Sets the stage of the application.
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the game manager of the application.
     *
     * @param gameManager
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    /**
     * Initializes the drag functionality on a given {@code GridPane} parameter.
     *
     * @param grid to be allowed to drag from
     */
    private void initTokenDrag(GridPane grid) {


        for (Node i : grid.getChildren()) {
            ImageView iv = (ImageView) i;

            iv.setOnDragDetected(event -> { // MouseEvent

                if (iv.getImage() == null) {
                    return;
                }
                if (gameManager.getGamePhase() != GamePhase.PLACEMENT && !grid.getId().equals(gameBoardGrid.getId())) {
                    return;
                }

                //draw check
                if (!gameManager.anyMovePossible() && gameManager.getGamePhase() != GamePhase.PLACEMENT) {
                    System.out.println("NO MORE MOVESSSSSSS!!!!!");
                    playerTurnLabel.setText("Draw, no more moves available");
                    return;
                }

                //MILL CHECK
                if (gameManager.isMill()) {
                    System.out.println("THERE IS A MILL");
                    //update label
                    playerTurnLabel.setText("Mill formed, " + gameManager.isOtherTurn().toString() + " can remove opponent token");
                    return;
                }

                //In movement phase, set the initial Others.Position of selected token
                if (gameManager.getGamePhase() == GamePhase.MOVEMENT) {
                    gameManager.setSelectedTokenPosition(getTilePosition(iv));
                }

                if (gameManager.colorOnTurn() == Colour.BLACK && iv.getId().contains("blk") ||
                        gameManager.colorOnTurn() == Colour.WHITE && iv.getId().contains("wht")) {

                    //check if all token has been placed on board yet or not
                    if (gameManager.getGamePhase() == GamePhase.PLACEMENT && grid.getId().equals(gameBoardGrid.getId())) {
                        System.out.println("NEED TO PLACE ALL TOKEN FIRST");
                        //update label
                        playerTurnLabel.setText("NEED TO PLACE ALL TOKEN FIRST");
                        return;
                    }

                    Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(iv.getImage());
                    content.putString(iv.getId());
                    db.setContent(content);
                    event.consume();
                }
            });

            iv.setOnDragDone(event -> { // DragEvent
                if (event.getTransferMode() == TransferMode.MOVE) {
                    iv.setImage(null);
                    if (grid.getId().equals(gameBoardGrid.getId())) {
                        iv.setId(null);
                    }
                }

                event.consume();
            });
        }
    }

    /**
     * Initializes the drop functionality on a given {@code GridPane} parameter.
     *
     * @param grid to be allowed to drop to
     */
    private void initTokenDrop(GridPane grid) {
        for (Node i : grid.getChildren()) {
            ImageView iv = (ImageView) i;

            iv.setOnDragOver(event -> { // DragEvent
                Dragboard db = event.getDragboard();
                if (event.getGestureSource() != iv && db.hasImage() && db.hasString() && iv.getId() == null) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            });

            iv.setOnDragDropped(event -> { // DragEvent
                Dragboard db = event.getDragboard();
                if (db.hasImage() && db.hasString()) {
                    if (iv.getId() == null) {
                        Position placePosition = getTilePosition(iv);
                        if (gameManager.validateTokenPlacement(placePosition)) {
                            iv.setImage(db.getImage());
                            iv.setId(db.getString());
                            board.setTokenPlacedPosition(placePosition);

                            afterTokenPlacementBoardUpdates(placePosition);

                            event.setDropCompleted(true);
                        } else {
                            System.out.print("CANNOT PLACE");
                            playerTurnLabel.setText("Others.Token cannot be placed here");
                        }
                    }
                }
                event.consume();
                if (!gameManager.isMill()) {
                    gameManager.setPlayer2TurnProperty(gameManager.getPlayer2().isTurn()); //AI auto place
                }
            });
        }
    }


    /**
     * Checks if the opponent's token clicked by the current player can be removed.
     * Tokens can only be removed if it is not part of a mill.
     */
    private void removeTileMill() {
        AtomicBoolean flag = new AtomicBoolean(false);
        for (ImageView iv : boardGridChildren) {
            iv.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (gameManager.isMill()) {
                    if (iv.getImage() != null && iv.getId() != null) {
                        System.out.println(gameManager.colorOnTurn());
                        if (iv.getId().contains("blk") && gameManager.colorOnTurn() == Colour.BLACK ||
                                iv.getId().contains("wht") && gameManager.colorOnTurn() == Colour.WHITE) {
                            Position position = getTilePosition(iv);

                            if (gameManager.removeToken(position)) {//if token can be removed

                                iv.setImage(null);
                                iv.setId(null);

                                gameManager.setMill(false);
                                playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn");
                                flag.set(true);
                                resetImagesOnRemovableTiles();



                            } else {
                                System.out.println("TOKEN CANNOT BE REMOVED");
                                //update label
                                playerTurnLabel.setText("Part of mill, cannot remove token");
                            }

                        }
                    }
                }
                //check win after the token is removed, win if opponent has less than 3 tokens
                gameWinCheck();
                event.consume();

                if (flag.get()) {
                    //Set to false, then true to trigger auto event
                    gameManager.setPlayer2TurnProperty(false);
                    gameManager.setPlayer2TurnProperty(gameManager.getPlayer2().isTurn());
                    flag.set(false);
                }
            });
        }
    }

    /**
     * After token removal, reset the images on the opponent's token that can be removed.
     *
     * Loops every tile on the board, if the tile is not empty and the tile contains the opponent's token that can be removed,
     * reset back the image to the original image.
     *
     */
    private void resetImagesOnRemovableTiles() {
        for (ImageView iv : boardGridChildren) {
            if (iv.getId() != null) {
                if (gameManager.colorOnTurn() == Colour.BLACK && iv.getId().contains("blk")) {
                    iv.setImage(new Image("/black_tile.png"));
                } else if (gameManager.colorOnTurn() == Colour.WHITE && iv.getId().contains("wht")) {
                    iv.setImage(new Image("/white_tile.png"));
                }
            }
        }
    }

    /**
     * If the current player forms a mill, the opponent's token that can be removed will be highlighted.
     *
     * Loops every tile on the board, if the tile is not empty and the tile contains the opponent's token that can be removed,
     * replace the image to indicate it is removable.
     */
    private void putImagesOnRemovableTiles(){
        for (ImageView iv : boardGridChildren) {
                if (gameManager.isMill()) {
                    System.out.println("putImagesOnRemovableTiles");
                    if (iv.getImage() != null && iv.getId() != null) {
                        System.out.println(gameManager.colorOnTurn());
                        if (iv.getId().contains("blk") && gameManager.colorOnTurn() == Colour.BLACK ||
                                iv.getId().contains("wht") && gameManager.colorOnTurn() == Colour.WHITE) {
                            Position position = getTilePosition(iv);

                            if (position != null && board.canBeRemoved(position, false)) {//if token can be removed
                                System.out.println("removing token image");
//                                iv.setImage(null);
                                if (iv.getId().contains("blk") && this.rootGameMode == GameMode.HUMAN) {
                                    iv.setImage(new Image("/black_tile_removable.png"));
                                } else if (iv.getId().contains("wht")) {
                                    iv.setImage(new Image("/white_tile_removable.png"));
                                }

                            } else {
                                System.out.println("TOKEN CANNOT BE REMOVED");
                            }

                        }
                    }
                }

        }

    }


    /**
     * Initializes the listeners for the properties of the game manager.
     */
    private void initGameManagerPropertyListeners() {

        final int[] count = {1};
        board.tokenPlacedPositionProperty().addListener((observableValue, oldPosition, newPosition) -> {
            System.out.println("attempt to form mill");
            putImagesOnRemovableTiles();
            if (newPosition != null && gameManager.getGamePhase() == GamePhase.PLACEMENT) {
                gameManager.placeToken(newPosition);
            } else if (gameManager.getGamePhase() == GamePhase.MOVEMENT) {
                gameManager.moveToken(newPosition);
            }
        });
        gameManager.player2TurnProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue && this.rootGameMode == GameMode.COMPUTER) {   //play against AI mode
                Task<Void> aiTask = new Task<Void>() {  //introduce a delay to make the AI move more realistic
                    @Override
                    protected Void call() throws Exception {
                        TimeUnit.MILLISECONDS.sleep(500 + new Random().nextInt(800));
                        Platform.runLater(() -> {
                            if (gameManager.getGamePhase() == GamePhase.PLACEMENT && !gameManager.isMill()) {
                                aiBasicPlacement2(count[0]);
                                count[0]++;
                                gameManager.setPlayer2TurnProperty(false);
                            } else if (gameManager.getGamePhase() == GamePhase.MOVEMENT && !gameManager.isMill()) {
                                aiMoveToken2();
                                gameManager.setPlayer2TurnProperty(false);
                            }
                        });
                        return null;
                    }
                };
                new Thread(aiTask).start();
            }
        });
    }

    /**
     * Initializes the controller class. This method is automatically called.
     * It is called after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        gameManager = new GameManager();
        board = gameManager.getBoard();
        boardGridChildren = FXCollections.observableArrayList();    //reinitialize the list for new game
        rightPocketGridChildren = FXCollections.observableArrayList();
        for (Node i : gameBoardGrid.getChildren()) {
            boardGridChildren.add((ImageView) i);
        }

        for (Node j : rightPocketGrid.getChildren()) {
            rightPocketGridChildren.add((ImageView) j);
        }

        //set image for rightPocketGrid, cannot use the application.css file for AI, else it won't remove the token
        for (Node k : rightPocketGrid.getChildren()) {
            ((ImageView) k).setImage(new Image("file:res/white_tile.png"));
        }

        initGameManagerPropertyListeners();

        initTokenDrag(leftPocketGrid); //id of grid pane in fxml file

//        initTokenDrag(rightPocketGrid);
        initTokenDrag(gameBoardGrid);
        initTokenDrop(gameBoardGrid);
        removeTileMill();


    }

    /**
     * Sets the scene to its default values as set at the very first start.
     */
    private void initWindow() {
        stage.getScene().getStylesheets().clear();
        stage.getScene().getStylesheets().add(Main.class.getResource("View/RootLayout.fxml").toExternalForm());
        playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn"); //set label to player 1 turn

//        if (gameManager.getGamePhase() == GamePhase.GAMEOVER) {
//            gameBoardGrid.getChildren().remove(24);
//        }

        for (ImageView iv : boardGridChildren) {
            iv.setId(null);
            iv.setImage(null);
        }
    }

    /**
     * Dialog box to show the action of new game/quit game button.
     *
     * @param title
     * @param header
     * @param content
     * @param id      id = 0 -> gameover: new game or exit to main menu
     *                id = 1 -> quit game
     *                id = 2 -> exit to main menu
     *                id = 3 -> new game from the game scene
     */

    private void gameDialog(String title, String header, String content, int id) throws IOException {
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(btnYes, btnNo);
        alert.showAndWait();
        if (id == 0) {
            if (alert.getResult() == btnYes) { //handle new game
                initWindow();
                initialize();//initalize a new game
//
                playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn");
//                board.setNewGame(true);
            } else {
                //exit to main menu
                sceneController = new SceneController();
                sceneController.switchToMainMenuScene(this.stage);
            }

        } else if (id == 1) {   //handle quit game
            if (alert.getResult() == btnYes) {
                Platform.exit();
            }
        } else if (id == 2) { //handle exit to main menu
            if (alert.getResult() == btnYes) {
                sceneController = new SceneController();
                sceneController.switchToMainMenuScene(this.stage);
            }
        } else if (id == 3) {  //handle restart during game
            if (alert.getResult() == btnYes) {
                initWindow();
                initialize();//initalize a new game

                playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn");
//                board.setNewGame(true);
            }
        }
    }

    /**
     * Handles the action of the game over button.
     */
    public void handleGameover() throws IOException {
        gameDialog("Game Over", "Do you want to start a new game?", "All progress will be lost.", 0);
    }

    /**
     * Handles the action of the new game button.
     */
    public void handleNewGame() throws IOException {
        gameDialog("New Game", "Are you sure you want to start a new game?", "All progress will be lost.", 3);
    }


    /**
     * Handles the action of closing the game.
     */
    public void handleClose() throws IOException {
        gameDialog("Quit Game", "Are you sure you want to quit?", "All progress will be lost.", 1);
    }

    /**
     * Handles the action of exit to main menu
     */
    public void handleMenu() throws IOException {
        gameDialog("Exit to Main Menu", "Are you sure you want to quit?", "All progress will be lost.", 2);
    }


    /**
     * Handles the action of the music button. To mute or unmute the music in the game scene.
     */
    public void handleMusic() {
        if (Main.mediaPlayer.isMute()) {
            musicLabel.setText("Play Music");
            Main.mediaPlayer.setMute(false);
            musicLabel.setText("Mute Music");
            //set menu item text

        } else {
            musicLabel.setText("Mute Music");
            Main.mediaPlayer.setMute(true);
            musicLabel.setText("Play Music");

        }

    }

    //_____________________________________________________________
    //AI TEST CODE
//
//    private void aiBasicPlacement(int count) {
//        for (ImageView iv : boardGridChildren) {
//            Others.Position position = getTilePosition(iv);
//            if (iv.getId() == null) {
//                iv.setImage(new Image("file:res/white_tile.png"));
//                iv.setId("wht" + Integer.toString(count));
//                board.setTokenPlacedPosition(position);
//
//                //remove token from rightpocketgrid after it is placed
//                for (ImageView iv2 : rightPocketGridChildren) {
//                    if (iv2.getId() != null) {
//                        iv2.getImage();
//                        if (iv2.getId().contains("wht")) {
//                            iv2.setImage(null);
//                            iv2.setId(null);
//
//                            break;
//                        }
//                    }
//                }
//                afterTokenPlacementBoardUpdates(position);
//
//                if (gameManager.isMill()) {
//                    aiRemoveToken();
//                }
//
//                return;
//            }
//        }
//    }

    /**
     * Handles AI token removal on board
     *
     */
    private void aiRemoveToken() {
        for (ImageView iv : boardGridChildren) {
            if (iv.getId() != null) {
                if (iv.getId().contains("blk") && gameManager.colorOnTurn() == Colour.BLACK) {
                    Position position = getTilePosition(iv);
                    if (gameManager.removeToken(position)) {//if token can be removed
                        iv.setImage(null);
                        iv.setId(null);
                        gameManager.setMill(false);
                        playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn");
                        gameWinCheck();
                        break;
                    }
                }
            }
        }
    }
//
//    private void aiMoveToken() {
//        for (ImageView iv : boardGridChildren) {
//            System.out.println(iv);
//            if (iv.getId() != null) {
//                if (iv.getId().contains("wht") && gameManager.colorOnTurn() == Colour.WHITE) {
//                    Others.Position currentPosition = getTilePosition(iv);
//                    List<Others.Position> possiblePositions = board.getValidPositions(currentPosition);
//                    for (Others.Position p : possiblePositions) {
//                        for (ImageView newIv : boardGridChildren) {
//                            if (newIv.getId() == null) {
//                                Others.Position newPosition = getTilePosition(newIv);
//                                if (newPosition.equals(p)) {
//
//                                    newIv.setId(iv.getId());
//                                    newIv.setImage(iv.getImage());
//
//                                    iv.setId(null);
//                                    iv.setImage(null);
//
//                                    board.setOldPosition(currentPosition);
//                                    board.setTokenPlacedPosition(newPosition);
//
//                                    afterTokenPlacementBoardUpdates(newPosition);
//
//                                    if (gameManager.isMill()) {
//                                        aiRemoveToken();
//                                    }
//
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * Handles AI token placement to board using random
     * @param count used for id generation of the image view
     */
    private void aiBasicPlacement2(int count) {
        Random random = new Random();

        while (true) {
            int num = random.nextInt(24);
            ImageView iv = boardGridChildren.get(num);

            Position position = getTilePosition(iv);
            if (iv.getId() == null) {
                iv.setImage(new Image("/white_tile.png"));
                iv.setId("wht" + Integer.toString(count));
                board.setTokenPlacedPosition(position);

                //remove token from rightpocketgrid after it is placed
                for (ImageView iv2 : rightPocketGridChildren) {
                    if (iv2.getImage() != null) {
                        iv2.getImage();
                        if (iv2.getId().contains("wht")) {
                            iv2.setImage(null);
//                            iv2.setId(null);

                            break;
                        }
                    }
                }
                afterTokenPlacementBoardUpdates(position);

                if (gameManager.isMill()) {
                    aiRemoveToken();
                }

                return;
            }
        }
    }

    /**
     * Handles AI token movement on board using random
     *
     */
    private void aiMoveToken2() {
        Random random = new Random();

        while (true) {
            int num = random.nextInt(24);
            ImageView iv = boardGridChildren.get(num);
            if (iv.getId() != null) {
                if (iv.getId().contains("wht") && gameManager.colorOnTurn() == Colour.WHITE) {
                    Position currentPosition = getTilePosition(iv);

                    //possible movement from current position
                    List<Position> possiblePositions = board.getValidPositions(currentPosition);
                    for (Position p : possiblePositions) {
                        for (ImageView newIv : boardGridChildren) {
                            if (newIv.getId() == null) { //find positions that are empty
                                Position newPosition = getTilePosition(newIv);

                                if (gameManager.getPlayer2().getTotalPiecesOnBoard() == 3) {
                                    p = newPosition;
                                }
                                if (newPosition.equals(p)) { //if possible position = empty

                                    //transfer token
                                    newIv.setId(iv.getId());
                                    newIv.setImage(iv.getImage());

                                    iv.setId(null);
                                    iv.setImage(null);

                                    //update backend
                                    board.setOldPosition(currentPosition);
                                    board.setTokenPlacedPosition(newPosition);

                                    afterTokenPlacementBoardUpdates(newPosition);

                                    if (gameManager.isMill()) {
                                        aiRemoveToken();
                                    }

                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates after token placement logic such as text, mill status etc
     * @param position new position where the token is at
     */
    public void afterTokenPlacementBoardUpdates(Position position) {
        gameManager.changePlayerTurn();
        System.out.println(gameManager.colorOnTurn() + " turn");
        playerTurnLabel.setText(gameManager.colorOnTurn() + "'s turn");

        gameManager.updateMillStatus(position);

        if (gameManager.isMill()) { //MILL FORMED
            //update label
            putImagesOnRemovableTiles();
            playerTurnLabel.setText("Mill formed, " + gameManager.isOtherTurn() + " can remove opponent token");
        }
    }

    /**
     * Check game win condition and show prompt if game won
     *
     */
    public void gameWinCheck() {

        if (gameManager.checkWin() > 0) {
            System.out.println("WIN");
            gameManager.setGamePhase(GamePhase.GAMEOVER);

            if (gameManager.checkWin() == 1) {
                System.out.println("Player 1 WIN");
                playerTurnLabel.setText("Player 1 WIN");
//                        gameManager.gameOver();
            } else {
                System.out.println("Player 2 WIN");
                playerTurnLabel.setText("Player 2 WIN");

//                        gameManager.gameOver();
            }
            try { //after game over, prompt user to play again or quit
                handleGameover();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Handles game mode HUMAN or COMPUTER
     * @param mode integer to set game mode
     */
    public void setGameMode(int mode){
        if (mode == 0){
            this.rootGameMode = GameMode.HUMAN;
            initTokenDrag(rightPocketGrid); //init drag and drop for player 2 if it is not AI
        } else{
            this.rootGameMode = GameMode.COMPUTER;
        }
    }


}
