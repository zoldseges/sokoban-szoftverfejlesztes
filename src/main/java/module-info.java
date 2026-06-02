module io.github.zoldseges {
    requires javafx.controls;
    requires javafx.fxml;

    exports io.github.zoldseges;
    exports io.github.zoldseges.ui;

    opens io.github.zoldseges to javafx.fxml;
    opens io.github.zoldseges.ui to javafx.fxml;
}
