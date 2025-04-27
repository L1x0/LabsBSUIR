package by.astakhau.arkanoid.controller.menu;

import com.almasb.fxgl.dsl.FXGL;
import javafx.event.ActionEvent;

public class DeathScreenController {
    public void onMainMenu(ActionEvent actionEvent) {
        FXGL.getGameController().gotoMainMenu();
    }

    public void onExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}
