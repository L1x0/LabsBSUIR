package by.astakhau.arkanoid;

import by.astakhau.arkanoid.controller.SceneUpdater;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getSettings;


public class Arkanoid extends GameApplication {
    SceneUpdater sceneUpdater = new SceneUpdater();

    @Override
    protected void initUI() {
//        Image backgroundImage = FXGL.image(Objects.requireNonNull(Arkanoid.class.getResource("background.jpg")));
//        ImageView backgroundView = new ImageView(backgroundImage);
//
//        backgroundView.setFitWidth(getSettings().getWidth());
//        backgroundView.setFitHeight(getSettings().getHeight());
//        backgroundView.setPreserveRatio(false);
//        backgroundView.setId("background");
//
//        FXGL.getGameScene().addUINode(backgroundView);
        sceneUpdater.uploadResource("main-menu.fxml");
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Arkanoid");
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setVersion("0.1");
    }

    public static void main(String[] args) {
        launch(args);
    }
}