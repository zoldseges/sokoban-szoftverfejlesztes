package io.github.zoldseges.controller;

import io.github.zoldseges.Navigator;
import io.github.zoldseges.sokoban.GameSession;
import io.github.zoldseges.sokoban.core.Direction;
import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;
import io.github.zoldseges.view.GridRenderer;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameController {

    private final Navigator navigator;
    private GameSession gameSession;
    private boolean isWon;

    //NOTE: we need to capture _one_ handler instance.
    //      multiple `this::keyEventHandler` expressions are likely to resolve to different instances
    private final EventHandler<KeyEvent> keyHandlerReference = this::keyEventHandler;

    @FXML
    private Canvas canvas;
    @FXML
    private Label wonOverlay;

    @FXML
    private void initialize() {
        //NOTE: Events go from scene to leaf _in focus_ then back to the scene. EventHandlers fire on the way back.
        //      Instead of juggling with focus to make sure our handler is on the dispatch path when we need it,
        //      we just make it so that we get every unclaimed (not consumed) event bubbling up to / reaching _our_ scene.
        //      see more: https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm#sthref13
        ObservableValue<Scene> sceneProperty = canvas.sceneProperty();
        sceneProperty.addListener((_, oldScene, newScene) -> {
            // detach the handler from the scene we are leaving
            if (oldScene != null) oldScene.removeEventHandler(KeyEvent.KEY_PRESSED, keyHandlerReference);
            // attach the handler to the scene we are joining
            if (newScene != null) newScene.addEventHandler(KeyEvent.KEY_PRESSED, keyHandlerReference);
        });

        Grid grid = gameSession.getGameState().getGrid();
        GridRenderer.render(this.canvas, grid, gameSession.getPlayerDirection());
    }

    public GameController(Navigator navigator, Level level) {
        this.navigator = navigator;
        this.gameSession = new GameSession(level);
        if (this.gameSession.getGameState().isWon()) {
            this.handleWin();
        };
    }

    @FXML
    public void keyEventHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            this.navigator.toLevelLibrary();
            keyEvent.consume();
        } else if (!(this.isWon)) {
            GameSession.Command cmd = commandFor(keyEvent.getCode());
            if (cmd != null) {
                keyEvent.consume();
                this.gameSession.dispatchCommand(cmd);
                GridRenderer.render(this.canvas,
                        this.gameSession.getGameState().getGrid(),
                        this.gameSession.getPlayerDirection()
                );
            }
        }
        if (this.gameSession.getGameState().isWon()) {
            this.handleWin();
        }
    }

    private static GameSession.Command commandFor(KeyCode code) {
        return switch (code) {
            case UP,    W -> new GameSession.Command.Move(Direction.UP);
            case DOWN,  S -> new GameSession.Command.Move(Direction.DOWN);
            case LEFT,  A -> new GameSession.Command.Move(Direction.LEFT);
            case RIGHT, D -> new GameSession.Command.Move(Direction.RIGHT);
            default -> null;
        };
    }

    private void handleWin() {
        this.isWon = true;
        this.wonOverlay.setVisible(true);
    }
}
