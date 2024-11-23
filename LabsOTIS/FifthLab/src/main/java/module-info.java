module by.astakhau.otis {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens by.astakhau.otis to javafx.fxml;
    exports by.astakhau.otis;
}