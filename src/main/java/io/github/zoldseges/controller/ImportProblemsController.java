package io.github.zoldseges.controller;

import io.github.zoldseges.Navigator;

import io.github.zoldseges.sokoban.core.Direction;
import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;
import io.github.zoldseges.sokoban.core.Pos;
import io.github.zoldseges.view.GridRenderer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImportProblemsController {
    //TODO: used at one single place, but lifed it here so you see.
    private static final int LABEL_MARKER_SIZE = 14;

    private final Navigator navigator;
    private final String fileName;
    private final String absolutePath;
    private final ImportProblem importProblem;

    @FXML
    Canvas previewCanvas;
    //TODO: set label to xsb path
    @FXML
    Label filePathLabel;
    @FXML
    ListView<Level.Violation> problemListView;
    //TODO: set overlay on invalid grid
    @FXML
    VBox gridErrorOverlay;
    @FXML
    Label gridErrorOverlayLabel;

    @FXML
    void onBack() {
        navigator.toLevelLibrary();
    };

    //NOTE: we passed Path instead of string because maybe we want to
    //      have an open dialouge here, and string might not be reliable.
    public ImportProblemsController(Navigator navigator,
                                    Path filePath,
                                    ImportProblem importProblem) {
        this.navigator = navigator;
        this.absolutePath = filePath.toString();
        this.fileName = filePath.getFileName().toString();
        this.importProblem = importProblem;
    }

    @FXML
    private void initialize() {
        switch (this.importProblem) {
            case ImportProblem.GridProblems gridV -> {
                if (gridV.violations().size() > 1) {
                    throw new IllegalStateException("Not implemented - consider refactoring core.violations");
                }
                showGridProblem(gridV.violations().getFirst());
            }

            case ImportProblem.LevelProblems levelV -> {
                showAllLevelProblems(levelV.grid(), levelV.violations());
            }
        }
        Platform.runLater(problemListView::requestFocus);
    }

    @FXML
    private void onListViewKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            keyEvent.consume();
            onBack();
        }
    }

    private void showGridProblem(Grid.Violation gridViolation) {
        this.gridErrorOverlayLabel.setText(ViolationText.forGrid(gridViolation));
        gridErrorOverlay.setVisible(true);
    }

    //NOTE: yes, grid is parameter is kind of misleading...
    private void showLevelProblem(Grid grid, Level.Violation violation) {
        GridRenderer.render(previewCanvas, grid, Direction.DOWN);
        if (violation != null) {
            List<Pos> positions = fromLevelViolation(violation);
            if (!(positions.isEmpty())) {
                GridRenderer.highlight(previewCanvas, grid, positions,
                        ColorHandler.tintOf(violation));
            }
        }
    }

    private void showAllLevelProblems(Grid grid, List<Level.Violation> violations) {
        gridErrorOverlay.setVisible(false);

        problemListView.setCellFactory(_ -> new LevelProblemCell());
        problemListView.setItems(FXCollections.observableArrayList(violations));

        problemListView.getSelectionModel().selectedItemProperty()
                .addListener((_, _, selected) -> {
                    showLevelProblem(grid, selected);
                });

        problemListView.getSelectionModel().selectFirst();
    }

    private static final class LevelProblemCell extends ListCell<Level.Violation> {
        @Override
        public void updateItem(Level.Violation violation, boolean empty) {
            super.updateItem(violation, empty);
            if (empty || violation == null) {
                this.setText(null);
                this.setGraphic(null);
            } else {
                this.setText(ViolationText.forLevelViolation(violation));
                this.setGraphic(LevelProblemCell.tintedLabelMarker(ColorHandler.tintOf(violation)));
                //TODO: we shouldn't need this at the first place
                this.setWrapText(true);
            }
        }
        private static Rectangle tintedLabelMarker(Color color) {
            return new Rectangle(LABEL_MARKER_SIZE, LABEL_MARKER_SIZE, color);
        }
    }

    private static List<Pos> fromLevelViolation(Level.Violation violation) {
        return switch (violation) {
            case Level.Violation.NoPlayer(): {
                yield List.of();
            }
            //NOTE: can't use the usual w/o paren label what I'm used to for some reason (no, not becase of the
            //      `yield syntax`
            case Level.Violation.MultiplePlayers(List<Pos> players): {
                yield players;
            }
            case Level.Violation.GoalBoxMismatch(List<Pos> boxes, List<Pos> goals): {
                List<Pos> all = new ArrayList<>();
                //NOTE: differen colors would be nice;
                all.addAll(boxes);
                all.addAll(goals);
                yield all;
            }
        };
    }

    //NOTE: I don't think theese belongs in this class
    private static final class ColorHandler {
        private static final double HIGHLIGH_ALPHA = 0.45;

        private static final Color NO_LOCATION_TINT = Color.rgb(100, 0, 0, HIGHLIGH_ALPHA);
        private static final Color PLAYER_TINT      = Color.rgb(100, 50, 50, HIGHLIGH_ALPHA);
        private static final Color MISMATCH_TINT    = Color.rgb(0, 100, 0, HIGHLIGH_ALPHA);

        private static Color tintOf(Level.Violation violation) {
            return switch (violation) {
                case Level.Violation.NoPlayer _        -> NO_LOCATION_TINT;
                case Level.Violation.MultiplePlayers _ -> PLAYER_TINT;
                case Level.Violation.GoalBoxMismatch _ -> MISMATCH_TINT;
            };
        }
    }

    private static final class ViolationText {

        private ViolationText() {}

        static String forGrid(Grid.Violation gridViolation) {
                String prefix = "Could not load, invalid grid:\n  ";
                String suffix = switch (gridViolation) {
                    case Grid.Violation.WidthZeroOrLess width   -> "Invalid width: " + width.width();
                    case Grid.Violation.HeightZeroOrLess height -> "Invalid height: " + height.height();
                };
                return prefix + suffix;
        }

        static String forLevelViolation(Level.Violation levelViolation) {
            return (switch (levelViolation) {
                case Level.Violation.NoPlayer _ ->
                        "No player on level";
                case Level.Violation.MultiplePlayers mP ->
                        "Multiple players (" + mP.playerPositions().size() + ")";
                case Level.Violation.GoalBoxMismatch gBM ->
                        "Box(" + gBM.boxPositions().size() + ")-Goal(" + gBM.goalPositions().size() + ")";
            });
        }

        private static List<String> forAllLevelViolations(List<Level.Violation> violations) {
            List<String> allDescriptions = new ArrayList<>();
            for (Level.Violation violation : violations) {
                allDescriptions.add(forLevelViolation(violation));
            }
            return allDescriptions;
        }
    }
}
