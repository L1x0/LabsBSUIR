package by.astakhau.arkanoid.model.game.component;

import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import by.astakhau.arkanoid.model.game.EntityType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

public class BuffComponent extends Component {
    private final int effectId;
    ArkanoidEntityFactory arkanoidEntityFactory;

    public BuffComponent(int effectId) {
        this.effectId = effectId;
        arkanoidEntityFactory = new ArkanoidEntityFactory();
    }

    public void voidSpecialEffect() {
        FXGL.getGameWorld().removeEntity(entity);
        switch (effectId) {
            case 1:
                FXGL.getGameWorld().getEntitiesByType(EntityType.PADDLE).get(0).setScaleX(2);

                FXGL.runOnce(() -> {
                    FXGL.getGameWorld().getEntitiesByType(EntityType.PADDLE).get(0).setScaleX(1);
                }, Duration.seconds(15));
                return;
            case 2:
                var paddle = FXGL.getGameWorld().getEntitiesByType(EntityType.PADDLE).get(0);
                FXGL.getGameWorld().addEntities(
                        arkanoidEntityFactory.createBall(new SpawnData(paddle.getX() - 20, paddle.getY() - 40)),
                        arkanoidEntityFactory.createBall(new SpawnData(paddle.getX() + 60, paddle.getY() - 40)));

                return;
            case 3:
                FXGL.getGameWorld().getEntitiesByType(EntityType.BRICK).forEach(brick ->
                        brick.getComponent(BrickHealthComponent.class).reduceHealth());

                return;
            case 4:
                FXGL.getGameWorld().getEntitiesByType(EntityType.BALL).forEach(ball ->
                        ball.getComponent(BallMovementComponent.class).timeFreeze());

                return;
            case 5:
                arkanoidEntityFactory.createBottomWallEntity();

                FXGL.runOnce(() ->
                        FXGL.getGameWorld().removeEntities(
                                FXGL.getGameWorld().getEntitiesByType(EntityType.WALL_BOTTOM)), Duration.seconds(15));
        }
    }
}
