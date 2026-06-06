package io.github.zoldseges;

import io.github.zoldseges.controller.GameController;
import io.github.zoldseges.controller.ImportProblem;
import io.github.zoldseges.controller.ImportProblemsController;
import io.github.zoldseges.controller.LevelLibraryController;

import io.github.zoldseges.sokoban.core.Level;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Navigator {

    private final Scene scene;
    private final LevelLibrary levelLibrary;

    public Navigator(LevelLibrary levelLibrary) {
        this.scene = new Scene(new Pane());
        this.levelLibrary = levelLibrary;
        this.toLevelLibrary();
    }

    public Scene getScene() {
        return this.scene;
    }

    public void toLevelLibrary() {
        swap("level-library.fxml", new LevelLibraryController(this, this.levelLibrary));
    }

    public void toGame(Level level) {
        swap("game.fxml", new GameController(this, level));
    }

    public void toImportProblems(Path filePath, ImportProblem importProblem) {
        swap("import-problems.fxml", new ImportProblemsController(this, filePath, importProblem));
    }

    private void swap(String fxmlFilename, Object controller) {
        //TODO: could be cached at Navigator instantiation
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlFilename));
        loader.setController(controller);
        try {
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load: " + fxmlFilename, e);
        }
    }
}
