package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.Arkanoid;
import by.astakhau.arkanoid.controller.SceneUpdater;
import com.almasb.fxgl.dsl.FXGL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class LevelCompleteController {
    @FXML
    public VBox box;
    @FXML
    public Button nextLevel;

    public void onNextLevel(ActionEvent actionEvent) {
        Arkanoid.nextLevel();
        FXGL.getGameController().startNewGame();
    }

    public void onMainMenu(ActionEvent actionEvent) {
        new SceneUpdater().uploadResource("main-menu.fxml");
    }

    @FXML
    public void initialize() {
        if (Arkanoid.getLevelNum() == 9) {
            box.getChildren().remove(nextLevel);
        }
    }
}
