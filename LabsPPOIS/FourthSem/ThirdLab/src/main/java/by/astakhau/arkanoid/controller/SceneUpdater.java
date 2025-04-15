package by.astakhau.arkanoid.controller;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class SceneUpdater {


    public void uploadResource(String fileName) {
        FXMLLoader fxmlLoader = new FXMLLoader(Arkanoid.class.getResource(fileName));

        try {
            Parent root = fxmlLoader.load();
            FXGL.getSceneService().getCurrentScene().getContentRoot().getChildren().add(root);
        } catch (IOException e) {
            VBox box = new VBox();

            Button exitButton = new Button("Exit");
            exitButton.setOnAction(event -> System.exit(0));

            TextField message = new TextField();
            message.setText("макет не найден,\n" + e.getMessage());

            box.getChildren().add(exitButton);
        }
    }
}
