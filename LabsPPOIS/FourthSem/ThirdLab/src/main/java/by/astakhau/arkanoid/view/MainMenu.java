package by.astakhau.arkanoid.view;

import by.astakhau.arkanoid.Arkanoid;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;

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
            FXGL.getNotificationService().pushNotification("Макет главного меню не найден");
        }
    }
}
