package by.astakhau.arkanoid;

import by.astakhau.arkanoid.model.data.config.AppConfig;
import by.astakhau.arkanoid.model.data.config.ConfigManager;
import by.astakhau.arkanoid.model.data.level.LevelManager;
import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import by.astakhau.arkanoid.model.game.EntityType;
import by.astakhau.arkanoid.model.game.component.BrickHealthComponent;
import by.astakhau.arkanoid.model.game.component.BuffComponent;
import by.astakhau.arkanoid.view.CustomSceneFactory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAudioPlayer;


public class Arkanoid extends GameApplication {
    LevelManager levelManager;
    ArkanoidEntityFactory arkanoidEntityFactory;
    Entity paddle;


    @Override
    protected void onPreInit() {
        arkanoidEntityFactory = new ArkanoidEntityFactory();
        FXGL.getGameScene().getContentRoot().setCursor(Cursor.DEFAULT);
        levelManager = new LevelManager();
    }

    @Override
    protected void initPhysics() {
        FXGL.onCollision(EntityType.PADDLE, EntityType.BUFF, (paddle, buff) -> {
            buff.getComponent(BuffComponent.class).voidSpecialEffect();
        });

        List<EntityType> targetTypes = List.of(
                EntityType.BRICK,
                EntityType.PADDLE,
                EntityType.WALL_BOTTOM,
                EntityType.WALL_LEFT,
                EntityType.WALL_RIGHT,
                EntityType.WALL_TOP);

        for (EntityType type : targetTypes) {
            FXGL.onCollision(EntityType.BALL, type, (ball, obj) -> {
                if (type == EntityType.BRICK) {
                    obj.getComponent(BrickHealthComponent.class).reduceHealth();
                }
                Sound winSound = getAssetLoader().loadSound("kick_sound.mp3");
                getAudioPlayer().playSound(winSound);
            });
        }
    }

    @Override
    protected void initGame() {
        FXGL.loopBGM("background_music.mp3");

        paddle = arkanoidEntityFactory.createPaddle(new SpawnData(350, 500));
        FXGL.getGameWorld().addEntity(paddle);

        FXGL.getGameWorld().addEntity(arkanoidEntityFactory.createBall(new SpawnData(350, 400)));
        FXGL.getGameWorld().addEntity(arkanoidEntityFactory.background());
        arkanoidEntityFactory.createBoundaryWalls();

        try {
            levelManager.drawLevelById(4);
        } catch (IllegalArgumentException e) {
            VBox box = new VBox();

            Button exitButton = new Button("Exit");
            exitButton.setOnAction(event -> System.exit(0));

            TextField message = new TextField();
            message.setText("не найден уровень,\n" + e.getMessage());

            box.getChildren().add(exitButton);

            FXGL.getSceneService().getCurrentScene().getContentRoot().getChildren().add(box);

            int x = 30;

            for (int i = 0; i < 9; i++) {
                FXGL.getGameWorld().addEntity(arkanoidEntityFactory.createBrick(new SpawnData(x, 30), 3));
                x += 60;
            }
        }
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(-300);
            }

            @Override
            protected void onAction() {
                if (paddle.getX() <= 10) {
                    paddle.getComponent(PhysicsComponent.class).setVelocityX(0);
                }
            }

            @Override
            protected void onActionEnd() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(300);
            }

            @Override
            protected void onAction() {
                if (paddle.getRightX() >= 585) {
                    paddle.getComponent(PhysicsComponent.class).setVelocityX(0);
                }
            }

            @Override
            protected void onActionEnd() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.RIGHT);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        ConfigManager configManager = new ConfigManager();

        AppConfig appConfig = configManager.getConfig();

        settings.setTitle(appConfig.getAppName());
        settings.setWidth(appConfig.getWidth());
        settings.setHeight(appConfig.getHeight());
        settings.setVersion(appConfig.getAppVersion());
        settings.setSceneFactory(new CustomSceneFactory());
        settings.setMainMenuEnabled(true);
        settings.setPixelsPerMeter(appConfig.getPixelsPerMeter());


    }

    public static void main(String[] args) {
        launch(args);
    }
}