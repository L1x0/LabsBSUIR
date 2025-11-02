package by.astakhau.arkanoid.model.game;

import by.astakhau.arkanoid.model.game.component.*;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class ArkanoidEntityFactory implements EntityFactory {
    double appWidth = FXGL.getAppWidth();
    double appHeight = FXGL.getAppHeight();

    @Spawns("Paddle")
    public Entity createPaddle(SpawnData data) {
        var physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return FXGL.entityBuilder(data)
                .type(EntityType.PADDLE)
                .at(data.getX(), data.getY())
                .with(new CollidableComponent(true))
                .with(physics)
                .viewWithBBox(FXGL.texture("paddle.png"))
                .build();
    }

    @Spawns("Right Wall")
    public void createRightWallEntity() {
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
    public void createLeftWallEntity() {
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
    public void createTopWallEntity() {
        PhysicsComponent physics = getPhysicsComponent();

        FXGL.entityBuilder()
                .type(EntityType.WALL_TOP)
                .at(0, 0)
                .viewWithBBox(new Rectangle(appWidth, 10))
                .with(new CollidableComponent(true))
                .with(physics)
                .buildAndAttach();
    }

    @Spawns("Bottom Wall")
    public Entity createBottomWallEntity() {
        var physics = getPhysicsComponent();

        return FXGL.entityBuilder()
                .type(EntityType.WALL_BOTTOM)
                .at(0, appHeight - 30)
                .viewWithBBox(new Rectangle(appWidth, 10))
                .with(new CollidableComponent(true))
                .with(physics)
                .buildAndAttach();
    }

    public void createBoundaryWalls() {
        createTopWallEntity();
        createRightWallEntity();
        createLeftWallEntity();
    }

    @Spawns("Ball")
    public Entity createBall(SpawnData data, float speedPx) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setFixtureDef(new FixtureDef()
                .density(0.3f)
                .restitution(1.0f)
                .friction(0.0f)
        );

        physics.setOnPhysicsInitialized(() -> {
            FXGL.getPhysicsWorld().setGravity(0, 0);

            float speedM = (float) (speedPx / FXGL.getSettings().getPixelsPerMeter());

            physics.setLinearVelocity(speedM, -speedM);

            physics.setAngularVelocity(0);
            physics.getBody().setLinearDamping(0.0f);
            physics.getBody().setAngularDamping(9999);
        });

        var texture = FXGL.texture("ball.png");
        texture.setFitHeight(300);
        texture.setFitWidth(150);
        texture.setPreserveRatio(true);

        texture.setTranslateX(-texture.getFitWidth() / 2.0);
        texture.setTranslateY(-texture.getFitHeight() / 2.0);

        return FXGL.entityBuilder(data)
                .type(EntityType.BALL)
                .at(data.getX(), data.getY())
                .with(physics)
                .with(new BallMovementComponent())
                .view(texture)
                .bbox(new HitBox(BoundingShape.circle(0)))
                .collidable()
                .build();
    }

    @Spawns("Background")
    public Entity background() {
        ImageView background = new ImageView(new Image("background.png"));

        background.setFitWidth(600);
        background.setFitHeight(600);

        return FXGL.entityBuilder()
                .view(background)
                .zIndex(-1)
                .buildAndAttach();

    }

    @Spawns("Brick")
    public Entity createBrick(SpawnData data, int health) {
        return FXGL.entityBuilder(data)
                .type(EntityType.BRICK)
                .at(data.getX(), data.getY())
                .viewWithBBox("brick.png")
                .with(new CollidableComponent(true))
                .with(getPhysicsComponent())
                .with(new BrickHealthComponent(health))
                .with(new OutOfBoundsComponent())
                .build();
    }

    @Spawns("Buff")
    public Entity createBuff(SpawnData data) {
        var randomNum = FXGL.random(1, 5);

        String textureURL = switch (randomNum) {
            case 1 -> "paddleWidth.png";
            case 2 -> "pow.png";
            case 3 -> "hitEverything.png";
            case 4 -> "death.png";
            case 5 -> "wall.png";
            default -> throw new IllegalStateException("Unexpected value: " + randomNum);
        };

        return FXGL.entityBuilder()
                .type(EntityType.BUFF)
                .viewWithBBox(FXGL.texture(textureURL))
                .at(data.getX(), data.getY())
                .scale(0.5, 0.5)
                .with(new CollidableComponent(true))
                .with(new OutOfBoundsComponent())
                .with(new BuffComponent(randomNum))
                .with(new BuffMovementComponent(100))
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
