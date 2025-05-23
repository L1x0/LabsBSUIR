module by.astakhau.arkanoid.thirdlab {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;

    opens by.astakhau.arkanoid to javafx.fxml;
    exports by.astakhau.arkanoid;

    opens assets.textures;
    opens assets.music;
    opens assets.sounds;

    opens by.astakhau.arkanoid.model.data;
    exports by.astakhau.arkanoid.model.data;

    opens by.astakhau.arkanoid.model.data.score;
    exports by.astakhau.arkanoid.model.data.score;

    exports by.astakhau.arkanoid.controller.menu;
    opens by.astakhau.arkanoid.controller.menu to javafx.fxml;
    exports by.astakhau.arkanoid.model.data.config;
    opens by.astakhau.arkanoid.model.data.config;
    exports by.astakhau.arkanoid.model.data.level;
    opens by.astakhau.arkanoid.model.data.level;
}