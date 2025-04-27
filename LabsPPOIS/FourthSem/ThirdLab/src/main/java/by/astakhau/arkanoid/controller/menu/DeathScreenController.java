package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.controller.SceneUpdater;
import javafx.event.ActionEvent;

public class DeathScreenController {
    public void onMainMenu(ActionEvent actionEvent) {
        new SceneUpdater().uploadResource("main-menu.fxml");
    }

    public void onExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}
