package by.astakhau.arkanoid;

import by.astakhau.arkanoid.controller.SceneUpdater;
import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import by.astakhau.arkanoid.model.game.CustomSceneFactory;
import by.astakhau.arkanoid.model.game.component.MovementComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;


public class Arkanoid extends GameApplication {
    Entity paddle;
    Entity background;

    @Override
    protected void initGame() {
        ArkanoidEntityFactory arkanoidEntityFactory = new ArkanoidEntityFactory();
        paddle = arkanoidEntityFactory.newPaddle(new SpawnData(350, 500));
        background = arkanoidEntityFactory.background();

        FXGL.getGameWorld().addEntities(paddle, background);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                paddle.getProperties().setValue("direction", MovementComponent.Direction.LEFT);
            }

            @Override
            protected void onActionEnd() {
                paddle.getProperties().setValue("direction", MovementComponent.Direction.NONE);
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                paddle.getProperties().setValue("direction", MovementComponent.Direction.RIGHT);
            }

            @Override
            protected void onActionEnd() {
                paddle.getProperties().setValue("direction", MovementComponent.Direction.NONE);
            }
        }, KeyCode.RIGHT);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Arkanoid");
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setVersion("0.1");
        settings.setSceneFactory(new CustomSceneFactory());
        settings.setMainMenuEnabled(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}