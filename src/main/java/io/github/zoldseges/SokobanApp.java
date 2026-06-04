package io.github.zoldseges;

import javafx.application.Application;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SokobanApp extends Application {

    @Override
    public void start(Stage stage) {
        //NOTE: loads ~/sokoban.json or path passed as first arg to the program
        LevelLibrary loadedLibrary = _loadLevelLibraryFromArgsOrDefault();
        Navigator navigator = new Navigator(loadedLibrary);
        stage.setScene(navigator.getScene());
        stage.setTitle("Sokoban");
        stage.show();
    }

    //TODO: temporary, just pass first arg to main to use different level library than ~/sokoban.json
    private LevelLibrary _loadLevelLibraryFromArgsOrDefault() {
        Path levelLibraryPath;
        //NOTE: this is how you get the passed arguments
        List<String> args = getParameters().getRaw();
        if (args.isEmpty()) {
            levelLibraryPath = Path.of(System.getProperty("user.home"), "sokoban.json");
        } else {
            levelLibraryPath = Path.of(args.getFirst());
        }
        LevelLibrary levelLibrary = new LevelLibrary(levelLibraryPath);
        if (Files.exists(levelLibraryPath)) {
            try {
                levelLibrary.loadAndSetEntries();
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "Could not read library at: `"
                                + levelLibraryPath + "`:\n"
                                + e.getMessage()
                ).showAndWait();
            }
        }
        return levelLibrary;
    }

    static void main(String[] args) {
        launch();
    }
}