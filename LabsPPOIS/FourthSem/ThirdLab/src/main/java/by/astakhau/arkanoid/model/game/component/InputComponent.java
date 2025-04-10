package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

public class InputComponent extends Component {
    @Override
    public void onAdded() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                entity.getProperties().setValue("direction", MovementComponent.Direction.LEFT);
            }

            @Override
            protected void onActionEnd() {
                entity.getProperties().setValue("direction", MovementComponent.Direction.NONE);
            }
        }, KeyCode.LEFT);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                entity.getProperties().setValue("direction", MovementComponent.Direction.LEFT);
            }

            @Override
            protected void onActionEnd() {
                entity.getProperties().setValue("direction", MovementComponent.Direction.NONE);
            }
        }, KeyCode.RIGHT);
    }
}
