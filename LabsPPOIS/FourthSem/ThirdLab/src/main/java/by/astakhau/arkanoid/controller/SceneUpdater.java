package by.astakhau.arkanoid.controller;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class SceneUpdater implements SceneService {


    public void uploadResource(String fileName) {
        FXMLLoader fxmlLoader = new FXMLLoader(Arkanoid.class.getResource(fileName));

        try {
            Parent root = fxmlLoader.load();
            FXGL.getSceneService().getCurrentScene().getContentRoot().getChildren().add(root);
        } catch (IOException e) {
            FXGL.getNotificationService().pushNotification("");
        }
    }
}
