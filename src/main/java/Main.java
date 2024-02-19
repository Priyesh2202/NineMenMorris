package main.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


/**
 * @author Hee Zhan Zhynn
 *
 * This class is used to start the game and initialise the game UI.
 *
 */


public class Main extends Application {

    public static final String GAME_NAME = "Nine Men Morris";
    public static final String GAME_VERSION = "1.0";
    public static final String FULL_NAME = GAME_NAME + " " + GAME_VERSION;
    public static MediaPlayer mediaPlayer;

    public static void main(String[] args) {


        launch(args);
    }

    /**
     * This method is used to start the game and initialise the game UI.
     *  Game is initialise to the Main Menu scene
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            //play music
            Media sound = new Media(Main.class.getResource("/musicRes/lofi2.mp3").toExternalForm());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(new Runnable() {    //loop music
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            mediaPlayer.play();

            //close program when click window close
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            URL url = Main.class.getResource("/View/MainMenu.fxml");
            System.out.println("URL: " + url);

            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/View/MainMenu.fxml")));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Nine Men Moris");
            primaryStage.getIcons().add(new Image("file:res/icon.png"));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}