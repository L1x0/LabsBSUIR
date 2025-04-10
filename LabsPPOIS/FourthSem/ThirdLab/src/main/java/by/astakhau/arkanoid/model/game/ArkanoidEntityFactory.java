package by.astakhau.arkanoid.model.game;

import by.astakhau.arkanoid.model.game.component.InputComponent;
import by.astakhau.arkanoid.model.game.component.MovementComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import javafx.scene.shape.Rectangle;

public class ArkanoidEntityFactory implements EntityFactory {
    @Spawns("Paddle")
    public Entity newPaddle(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.PADDLE)
                .at(data.getX(), data.getY())
                .view(new Rectangle(40, 40) )
//                .with(new MovementComponent())
//                .with(new InputComponent())
                .build();
    }
}
