package by.astakhau.arkanoid.model.game;

import by.astakhau.arkanoid.model.game.component.BallMovementComponent;
import by.astakhau.arkanoid.model.game.component.BrickHealthComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class ArkanoidEntityFactory implements EntityFactory {
    double appWidth = FXGL.getAppWidth();
    double appHeight = FXGL.getAppHeight();

    @Spawns("Paddle")
    public Entity newPaddle(SpawnData data) {
        var physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return FXGL.entityBuilder(data)
                .type(EntityType.PADDLE)
                .at(data.getX(), data.getY())
                .with(physics)
                .viewWithBBox(FXGL.texture("paddle.png"))
                .build();
    }

    @Spawns("Right Wall")
    public void RightWallEntity() {
        PhysicsComponent physics = getPhysicsComponent();

        FXGL.entityBuilder()
                .type(EntityType.WALL_RIGHT)
                .at(appWidth - 10, 0)
                .viewWithBBox(new Rectangle(10, appHeight))
                .with(new CollidableComponent(true))
                .with(physics)
                .buildAndAttach();
    }

    @Spawns("Left Wall")
    public void LeftWallEntity() {
        PhysicsComponent physics = getPhysicsComponent();

        FXGL.entityBuilder()
                .type(EntityType.WALL_LEFT)
                .at(-2, 0)
                .viewWithBBox(new Rectangle(10, appHeight))
                .with(new CollidableComponent(true))
                .with(physics)
                .buildAndAttach();
    }

    @Spawns("Top Wall")
    public void TopWallEntity() {
        PhysicsComponent physics = getPhysicsComponent();

        FXGL.entityBuilder()
                .type(EntityType.WALL_TOP)
                .at(0, 0)
                .viewWithBBox(new Rectangle(appWidth, 10))
                .with(new CollidableComponent(true))
                .with(physics)
                .buildAndAttach();
    }

    public void createBoundaryWalls() {
        TopWallEntity();
        RightWallEntity();
        LeftWallEntity();
    }

    @Spawns("Ball")
    public Entity newBall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setFixtureDef(new FixtureDef()
                .density(0.3f)
                .restitution(1.0f)
                .friction(0.0f)
        );

        physics.setOnPhysicsInitialized(() -> {
            FXGL.getPhysicsWorld().setGravity(0, 0);

            float speedPx = 300;
            float speedM = (float) (speedPx / FXGL.getSettings().getPixelsPerMeter());

            physics.setLinearVelocity(speedM, -speedM);

            physics.setAngularVelocity(0);
            physics.getBody().setLinearDamping(0.0f);
            physics.getBody().setAngularDamping(9999);
        });

        return FXGL.entityBuilder(data)
                .type(EntityType.BALL)
                .at(data.getX(), data.getY())
                .with(physics)
                .with(new BallMovementComponent())
                .viewWithBBox(new Circle(10, Color.WHITESMOKE))
                .collidable()
                .build();
    }

    @Spawns("background")
    public Entity background() {
        ImageView background = new ImageView(new Image("background.jpg"));

        background.setFitWidth(600);
        background.setFitHeight(600);

        return FXGL.entityBuilder()
                .view(background)
                .zIndex(-1)
                .buildAndAttach();

    }

    @Spawns("brick")
    public Entity createBrick(SpawnData data, int health) {
        return FXGL.entityBuilder(data)
                .type(EntityType.BRICK)
                .at(data.getX(), data.getY())
                .viewWithBBox("brick.png")
                .with(new CollidableComponent(true))
                .with(getPhysicsComponent())
                .with(new BrickHealthComponent(health))
                .build();
    }

    private static PhysicsComponent getPhysicsComponent() {
        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.STATIC);
        physics.setFixtureDef(new FixtureDef()
                .density(0.0f)
                .restitution(1.0f)
                .friction(0.0f)
        );
        return physics;
    }
}
