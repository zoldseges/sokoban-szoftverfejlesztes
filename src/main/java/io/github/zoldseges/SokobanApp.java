package io.github.zoldseges;

import javafx.application.Application;

import javafx.stage.Stage;

public class SokobanApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Navigator navigator = new Navigator();
        stage.setScene(navigator.getScene());
        stage.setTitle("Sokoban");
        stage.show();
    }

    //TODO: can we actually delete main from SokobanApp?
    static void main(String[] args) {
        launch();
    }
}