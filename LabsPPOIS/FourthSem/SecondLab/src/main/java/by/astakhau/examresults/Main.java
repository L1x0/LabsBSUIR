package by.astakhau.examresults;

import by.astakhau.examresults.model.service.XmlStudentRepository;
import com.github.javafaker.Faker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dataManager.fxml"));
        Parent root = loader.load();

        stage.setTitle("Student Manager");
        stage.setScene(new Scene(root, 1230, 400));
        stage.show();
    }


    public static void main(String[] args) {
  //      launch();
        String[] subjects = new String[] {"Математика", "Русский язык", "Математический анализ", "Логика", "Химия",
        "Физика", "Английский язык" , "Философия", "Гимнастика", "Прыжки в длину"};
        Faker faker = new Faker();
        launch();
    }
}