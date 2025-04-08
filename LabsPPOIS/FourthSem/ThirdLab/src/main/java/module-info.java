module by.astakhau.arkanoid.thirdlab {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens by.astakhau.arkanoid to javafx.fxml;
    exports by.astakhau.arkanoid;
    exports by.astakhau.arkanoid.controller;
    opens by.astakhau.arkanoid.controller to javafx.fxml;
}