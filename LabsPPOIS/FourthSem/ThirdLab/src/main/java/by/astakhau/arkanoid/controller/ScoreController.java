package by.astakhau.arkanoid.controller;

import by.astakhau.arkanoid.Arkanoid;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class ScoreController {
    public void onGoBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Arkanoid.class.getResource("main-menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);

            scene.getStylesheets()
                    .addAll(this.getClass().getResource("/by/astakhau/arkanoid/style.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("fxml файл не найден");
        }
    }
}
