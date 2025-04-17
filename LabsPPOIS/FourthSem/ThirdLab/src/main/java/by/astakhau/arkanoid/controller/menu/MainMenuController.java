package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.controller.SceneService;
import by.astakhau.arkanoid.controller.SceneUpdater;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXML;

public class MainMenuController {
    SceneService sceneUpdater = new SceneUpdater();

    @FXML
    private void onStartGame() {
        FXGL.getGameController().startNewGame();
    }

    @FXML
    protected void onShowRecords() {
        sceneUpdater.uploadResource("score-page.fxml");
    }

    @FXML
    protected void onShowRules() {
        sceneUpdater.uploadResource("rules-page.fxml");
    }

    @FXML
    protected void onExit() {
        System.exit(0);
    }
}