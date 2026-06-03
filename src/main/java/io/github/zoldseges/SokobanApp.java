package io.github.zoldseges;

import io.github.zoldseges.controller.GameController;

import io.github.zoldseges.persistence.LevelLoader;
import io.github.zoldseges.sokoban.GameSession;
import io.github.zoldseges.sokoban.core.Level;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class SokobanApp extends Application {

    //TODO: temporary for testing (see commented out section below)
    //      production start shouldn't throw Exception
    @Override
    public void start(Stage stage) throws Exception {
        System.err.println("Warning, running the testing SokobanApp.start");
        Level level = LevelLoader.load(
                Path.of(SokobanApp.class
                        .getResource("simple1.xsb")
                        .toURI())
        );

        GameSession session = new GameSession(level);

        FXMLLoader fxmlLoader = new FXMLLoader(
                GameController.class.getResource("game.fxml")
        );
        Parent root = fxmlLoader.load();

        GameController gameController = fxmlLoader.getController();
        gameController.setGameSession(session);

        Scene gameScene = new Scene(root, 640, 480);
        stage.setScene(gameScene);
        stage.setTitle("Sokoban");
        stage.show();
    }

    //    @Override
//    public void start(Stage stage) throws IOException {
//
//        // TODO: placeholder
//
//        // create GameSession
//
//        // load fxml
//        var fxmlFilename = "game.fxml";
//        var fxmlLoader = new FXMLLoader(
//                GameController.class.getResource(fxmlFilename)
//        );
//        Parent root = fxmlLoader.load();
//        SokobanApp.scene = new Scene(root, 640, 480);
//
//        scene = new Scene(root, 640, 480);
//        stage.setScene(scene);
//        stage.show();
//    }

    // TODO: delete setRoot if not useful
//    static void setRoot(String fileName) throws IOException {
//        scene.setRoot(new FXMLLoader(GameController.class.getResource(fileName)).load());
//    }

    //TODO: can we actually delete this?
    public static void main(String[] args) {
        launch();
    }
}