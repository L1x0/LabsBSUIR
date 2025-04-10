package by.astakhau.arkanoid.model;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class MainMenu extends FXGLMenu {

    public MainMenu(MenuType type) {
        super(type);

        try {
            FXMLLoader loader = new FXMLLoader(Arkanoid.class.getResource("main-menu.fxml"));
            Parent root = loader.load();

            getContentRoot().getChildren().add(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
