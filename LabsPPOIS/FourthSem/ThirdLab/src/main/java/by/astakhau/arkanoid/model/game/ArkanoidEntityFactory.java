package by.astakhau.arkanoid.model.game;

import by.astakhau.arkanoid.model.game.component.MovementComponent;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ArkanoidEntityFactory implements EntityFactory {
    @Spawns("Paddle")
    public Entity newPaddle(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.PADDLE)
                .at(data.getX(), data.getY())
                .with(new MovementComponent())
                .view(new Rectangle(80, 15, Color.BLUE))
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
}
