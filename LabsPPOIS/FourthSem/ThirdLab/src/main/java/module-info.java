module by.astakhau.arkanoid.thirdlab {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires annotations;
    requires java.desktop;

    opens by.astakhau.arkanoid to javafx.fxml;
    exports by.astakhau.arkanoid;

    opens assets.textures;

    exports by.astakhau.arkanoid.controller.menu;
    opens by.astakhau.arkanoid.controller.menu to javafx.fxml;
}