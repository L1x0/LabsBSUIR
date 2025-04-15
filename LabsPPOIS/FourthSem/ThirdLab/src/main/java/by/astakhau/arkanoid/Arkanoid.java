package by.astakhau.arkanoid;

import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import by.astakhau.arkanoid.model.game.EntityType;
import by.astakhau.arkanoid.model.game.component.BrickHealthComponent;
import by.astakhau.arkanoid.view.CustomSceneFactory;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;


public class Arkanoid extends GameApplication {
    Entity paddle;
    Entity background;
    Entity ball;

    @Override
    protected void onPreInit() {
        FXGL.getGameScene().getContentRoot().setCursor(Cursor.DEFAULT);
    }

    @Override
    protected void initPhysics() {
        FXGL.onCollision(EntityType.BALL, EntityType.BRICK, (ball, brick) -> {
            brick.getComponent(BrickHealthComponent.class).reduceHealth();
        });
    }

    @Override
    protected void initGame() {
        ArkanoidEntityFactory arkanoidEntityFactory = new ArkanoidEntityFactory();
        paddle = arkanoidEntityFactory.newPaddle(new SpawnData(350, 500));
        ball = arkanoidEntityFactory.newBall(new SpawnData(350, 400));
        background = arkanoidEntityFactory.background();
        arkanoidEntityFactory.createBoundaryWalls();

        var Brick = arkanoidEntityFactory.createBrick(new SpawnData(30, 30), 3);

        FXGL.getGameWorld().addEntities(ball, paddle, background, Brick);
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