package io.github.zoldseges.controller;

import io.github.zoldseges.LevelLibrary;
import io.github.zoldseges.Navigator;

import io.github.zoldseges.persistence.LevelLibraryEntry;

import io.github.zoldseges.persistence.Xsb;
import io.github.zoldseges.sokoban.core.Direction;
import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;
import io.github.zoldseges.sokoban.core.Pos;
import io.github.zoldseges.view.GridRenderer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LevelLibraryController {

    private final Navigator navigator;
    private final LevelLibrary levelLibrary;

    @FXML
    private Label libPathLabel;
    @FXML
    private ListView<LevelLibraryEntry> levelList;
    @FXML
    private Canvas previewCanvas;

    public LevelLibraryController(Navigator navigator, LevelLibrary levelLibrary) {
        this.navigator = navigator;
        this.levelLibrary = levelLibrary;
    }

    @FXML
    private void initialize() {
        levelList.setItems(levelLibrary.getEntries());
        // we need to enable ourself to be able to create new entry-cells in our ListView
        levelList.setCellFactory(_ -> new LevelEntryCell());
        levelList.getSelectionModel().selectedItemProperty()
                .addListener((_, _, selected) -> {
                    if (selected != null) {
                        GridRenderer.render(previewCanvas, selected.level().copyGrid(), Direction.DOWN);
                    } else {
                        previewCanvas.getGraphicsContext2D();
                    }
        });
        if (!(levelLibrary.getEntries().isEmpty())) {
            levelList.getSelectionModel().selectFirst();
        }
        Platform.runLater(levelList::requestFocus);
    }

    private static class LevelEntryCell extends ListCell<LevelLibraryEntry> {
        //NOTE: this is the JavaFX documented way to change how a list renders almost to the T.
        //      see: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html#updateItem-T-boolean-
        @Override
        public void updateItem(LevelLibraryEntry entry, boolean empty) {
            super.updateItem(entry, empty);
            if (empty || entry == null) {
                this.setText(null);
            } else {
                this.setText(entry.name());
            }
        }
    }

    @FXML
    private void onPlay() {
        LevelLibraryEntry entry = levelList.getSelectionModel().getSelectedItem();
        if (entry != null) {
            navigator.toGame(entry.level());
        }
    }

    @FXML
    private void onListViewKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            onPlay();
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            onDelete();
        }
        keyEvent.consume();
    }

    @FXML
    private void onDelete() {
        LevelLibraryEntry entry = levelList.getSelectionModel().getSelectedItem();
        if (entry != null) {
            try {
                levelLibrary.removeEntry(entry);
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    }

    private void handleIOException(IOException e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }

    @FXML
    private void onXsbImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import .xsb level");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Sokoban XSB", "*.xsb")
        );

        File file = fileChooser.showOpenDialog(navigator.getScene().getWindow());
        if (file == null) return;

        String fileName = file.getName();
        String xsbText;
        try {
            xsbText = Files.readString(file.toPath());
        } catch (IOException e) {
            handleIOException(e);
            return;
        }
        parseXsbStage(xsbText, fileName);
    }

    //FIXME: quickfix before midnight: duplicate entries
    private void addToLibrary(LevelLibraryEntry entry) {
        LevelLibraryEntry duplicateEntry = null;
        for (LevelLibraryEntry iterEntry : this.levelLibrary.getEntries()) {
            if (iterEntry.name().equals(entry.name())) {
                duplicateEntry = iterEntry;
                break;
            }
        }
        if (duplicateEntry != null) {
            this.levelList.getSelectionModel().select(duplicateEntry);
            return;
        }
        // FIXME: quicfix end
        try {
            this.levelLibrary.addEntry(entry);
            this.levelList.getSelectionModel().select(entry);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void parseXsbStage(String xsbText, String fileName) {
        switch (Xsb.gridFrom(xsbText)) {
            case Grid.Result.Err err -> reportGridErrors(fileName, err.violations());
            case Grid.Result.Ok ok -> validateStage(fileName, ok.grid());
        };
    }

    private void validateStage(String fileName, Grid grid) {
        switch (Level.from(grid)) {
            case Level.Result.Err err -> reportLevelErrors(fileName, err.violations());
            case Level.Result.Ok ok   ->
                    this.addToLibrary(
                            new LevelLibraryEntry(fileName, ok.level())
                    );
        }
    }

    //TODO: this part should highlight violations on preview instead until end of ViolationText
    private static void reportGridErrors(String fileName, List<Grid.Violation> violations) {
        String msg = ViolationText.forGrid(fileName, violations);
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private static void reportLevelErrors(String fileName, List<Level.Violation> violations) {
        String msg = ViolationText.forLevel(fileName, violations);
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    private static final class ViolationText {

        private ViolationText() {}

        static String forGrid(String name, List<Grid.Violation> violations) {
            List<String> lines = new ArrayList<>();
            for (Grid.Violation violation : violations) {
                lines.add(describe(violation));
            }
            return summary("Invalid grid: '" + name + "'", lines);
        }

        static String forLevel(String name, List<Level.Violation> violations) {
            List<String> lines = new ArrayList<>();
            for (Level.Violation violation : violations) {
                lines.add(describe(violation));
            }
            return summary("Invalid map: '" + name + "'", lines);
        }

        private static String summary(String prefix, List<String> lines) {
            int lineCount = lines.size();
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(" - found ").append(lineCount).append(" problem:");
            for (String line : lines) {
                sb.append("\n").append(line);
            }
            return sb.toString();
        }

        private static String describe(Grid.Violation violation) {
            return switch (violation) {
                case Grid.Violation.WidthZeroOrLess width   -> "Invalid width: " + width.width();
                case Grid.Violation.HeightZeroOrLess height -> "Invalid height: " + height.height();
            };
        }

        private static String describe(Level.Violation violation) {
            return switch (violation) {
                case Level.Violation.NoPlayer _ -> "No player on level";
                case Level.Violation.MultiplePlayers mP ->
                        "Multiple players (" + mP.playerPositions().size() + ") at \n"
                                + strFromPosList(mP.playerPositions());
                case Level.Violation.GoalBoxMismatch gBM ->
                        "Box (" + gBM.boxPositions().size() + ") "
                                + "Goal (" + gBM.goalPositions().size() + ") mismatch:\n"
                                + "- Boxes at:" + strFromPosList(gBM.boxPositions()) + "\n"
                                + "- Goals at:" + strFromPosList(gBM.goalPositions());
            };
        }

        private static String strFromPosList(List<Pos> positions) {
            if (!(positions.isEmpty())) {
                StringBuilder sb = new StringBuilder();
                sb.append('{');
                for (int i = 0; i < positions.size(); ++i) {
                    Pos pos = positions.get(i);
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append("[").append(pos.x())
                            .append(", ")
                            .append(pos.y()).append("]");
                }
                sb.append('}');
                return sb.toString();
            } else {
                return "none";
            }
        }
    }

}
