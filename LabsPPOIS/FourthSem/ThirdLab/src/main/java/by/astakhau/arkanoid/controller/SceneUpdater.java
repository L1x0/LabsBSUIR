package by.astakhau.arkanoid.controller;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class SceneUpdater {


    public void uploadResource(String fileName) {
        FXGL.getGameScene().clearUINodes();

        FXMLLoader fxmlLoader = new FXMLLoader(Arkanoid.class.getResource(fileName));
        try {
            Parent root = fxmlLoader.load();

            FXGL.getGameScene().addUINode(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Не найден макет главного меню");
            alert.showAndWait();
        }
    }
}
