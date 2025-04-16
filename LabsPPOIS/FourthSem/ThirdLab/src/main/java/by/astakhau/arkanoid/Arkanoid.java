package by.astakhau.arkanoid;

import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import by.astakhau.arkanoid.model.game.EntityType;
import by.astakhau.arkanoid.model.game.component.BrickHealthComponent;
import by.astakhau.arkanoid.model.game.component.BuffComponent;
import by.astakhau.arkanoid.view.CustomSceneFactory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;


public class Arkanoid extends GameApplication {
    ArkanoidEntityFactory arkanoidEntityFactory;
    Entity paddle;
    Entity background;
    Entity ball;

    @Override
    protected void onPreInit() {
        arkanoidEntityFactory = new ArkanoidEntityFactory();
        FXGL.getGameScene().getContentRoot().setCursor(Cursor.DEFAULT);
    }

    @Override
    protected void initPhysics() {
        FXGL.onCollision(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {
            brick.getComponent(BrickHealthComponent.class).reduceHealth();
        });

        FXGL.onCollision(EntityType.PADDLE, EntityType.BUFF, (paddle, buff) -> {
            buff.getComponent(BuffComponent.class).voidSpecialEffect();
        });
    }

    @Override
    protected void initGame() {
        paddle = arkanoidEntityFactory.createPaddle(new SpawnData(350, 500));
        ball = arkanoidEntityFactory.createBall(new SpawnData(350, 400));
        background = arkanoidEntityFactory.background();
        arkanoidEntityFactory.createBoundaryWalls();
        int x = 30;

        for (int i = 0; i < 9; i++) {
            FXGL.getGameWorld().addEntity(arkanoidEntityFactory.createBrick(new SpawnData(x, 30), 1));
            x += 60;
        }

        FXGL.getGameWorld().addEntities(ball, paddle, background);
    }

    @Override
    protected void initInput() {

        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(-300);
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
            protected void onActionEnd() {
                paddle.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.RIGHT);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Arkanoid");
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setVersion("0.3");
        settings.setSceneFactory(new CustomSceneFactory());
        settings.setMainMenuEnabled(true);
        settings.setPixelsPerMeter(32);
    }

    public static void main(String[] args) {
        launch(args);
    }
}