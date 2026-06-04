package io.github.zoldseges;

import io.github.zoldseges.controller.GameController;
import io.github.zoldseges.controller.LevelLibraryController;
import io.github.zoldseges.sokoban.core.Level;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Navigator {

    private final Scene scene;

    public Navigator() {
        this.scene = new Scene(new Pane());
        this.toLevelLibrary();
    }

    public Scene getScene() {
        return this.scene;
    }

    public void toLevelLibrary() {
        swap("level-library.fxml", new LevelLibraryController(this));
    }

    public void toGame(Level level) {
        swap("game.fxml", new GameController(this, level));
    }

    private void swap(String fxmlFilename, Object controller) {
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlFilename));
        loader.setController(controller);
        try {
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load %s:%s%n".formatted(fxmlFilename, e));
        }
    }
}
