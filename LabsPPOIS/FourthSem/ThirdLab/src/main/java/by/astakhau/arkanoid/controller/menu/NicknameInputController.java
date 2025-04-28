package by.astakhau.arkanoid.controller.menu;

import by.astakhau.arkanoid.Arkanoid;
import by.astakhau.arkanoid.model.data.score.Player;
import com.almasb.fxgl.dsl.FXGL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NicknameInputController {
    @FXML
    public TextField nicknameField;

    public void onSubmitNickname(ActionEvent actionEvent) {
        Player player = new Player();

        player.setName(nicknameField.getText());
        player.setScore(0);

        Arkanoid.setPlayer(player);

        Arkanoid.levelReset();
        FXGL.getGameController().startNewGame();
    }
}
