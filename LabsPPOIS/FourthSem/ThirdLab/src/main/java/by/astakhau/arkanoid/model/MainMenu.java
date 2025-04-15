package by.astakhau.arkanoid.model;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainMenu extends FXGLMenu {

    public MainMenu(MenuType type) {
        super(type);

        try {
            FXMLLoader loader = new FXMLLoader(Arkanoid.class.getResource("main-menu.fxml"));
            Parent root = loader.load();

            getContentRoot().getChildren().add(root);
            getContentRoot().setCursor(Cursor.DEFAULT);
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
