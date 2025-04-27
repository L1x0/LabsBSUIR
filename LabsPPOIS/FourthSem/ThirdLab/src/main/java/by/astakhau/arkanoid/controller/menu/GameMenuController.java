package by.astakhau.arkanoid.controller.menu;

import com.almasb.fxgl.dsl.FXGL;
import javafx.event.ActionEvent;

public class GameMenuController {

    public void onReturnToMainMenu(ActionEvent actionEvent) {
        FXGL.getGameController().gotoMainMenu();
    }

    public void onExit(ActionEvent actionEvent) {
        System.exit(0);
    }
}
