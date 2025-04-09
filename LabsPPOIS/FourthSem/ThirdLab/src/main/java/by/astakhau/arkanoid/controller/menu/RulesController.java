package by.astakhau.arkanoid.controller.menu;



import by.astakhau.arkanoid.controller.SceneUpdater;
import javafx.fxml.FXML;

public class RulesController {
    SceneUpdater sceneUpdater = new SceneUpdater();

    @FXML
    public void onGoBack() {
       sceneUpdater.uploadResource("main-menu.fxml");
    }
}
