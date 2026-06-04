module io.github.zoldseges {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires com.fasterxml.jackson.databind;

    exports io.github.zoldseges;
    exports io.github.zoldseges.controller;

    opens io.github.zoldseges to javafx.fxml;
    opens io.github.zoldseges.controller to javafx.fxml;
}
