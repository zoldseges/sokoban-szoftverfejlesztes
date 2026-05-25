module io.github.zoldseges {
    requires javafx.controls;
    requires javafx.fxml;

    opens io.github.zoldseges to javafx.fxml;
    exports io.github.zoldseges;
}
