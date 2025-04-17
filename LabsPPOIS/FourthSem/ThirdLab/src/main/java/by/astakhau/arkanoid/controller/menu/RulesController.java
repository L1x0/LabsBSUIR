package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.controller.SceneService;
import by.astakhau.arkanoid.controller.SceneUpdater;
import javafx.fxml.FXML;

public class RulesController {
    SceneService sceneUpdater = new SceneUpdater();

    @FXML
    public void onGoBack() {
       sceneUpdater.uploadResource("main-menu.fxml");
    }
}
