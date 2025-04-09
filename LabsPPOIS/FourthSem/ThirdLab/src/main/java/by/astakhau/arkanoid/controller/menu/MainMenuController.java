package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.controller.SceneUpdater;
import javafx.fxml.FXML;

public class MainMenuController {
    SceneUpdater sceneUpdater = new SceneUpdater();

    @FXML
    protected void onStartGame() {
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