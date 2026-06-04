package io.github.zoldseges.controller;

import io.github.zoldseges.Navigator;
import io.github.zoldseges.SokobanApp;
import io.github.zoldseges.persistence.LevelLoader;
import io.github.zoldseges.sokoban.core.Level;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.nio.file.Path;

public class LevelLibraryController {

    private final Navigator navigator;

    @FXML
    private Label libPathLabel;
    //TODO: it should be ListView<LevelEntry>
    @FXML
    private ListView levelList;
    @FXML
    private Canvas preview;

    public LevelLibraryController(Navigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void onPlay() {
        //TODO: this is temporary
        System.err.println("Warning, running the testing SokobanApp.start");
        Level level = null;
        try {
            level = LevelLoader.load(
                    Path.of(SokobanApp.class
                            .getResource("simple1.xsb")
                            .toURI())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // until here
        navigator.toGame(level);
    }
}
