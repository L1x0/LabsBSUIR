package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.entity.component.Component;
import lombok.Getter;
import lombok.Setter;

public class MovementComponent extends Component {
    private final double speed = 100;
    @Setter Direction direction = Direction.NONE;

    @Getter
    public enum Direction {
        LEFT(-1),
        RIGHT(1),
        NONE(0);

        private final int value;

        Direction(int value) {
            this.value = value;
        }

    };

    @Override
    public void onUpdate(double tpf) {
        direction = entity.getProperties().getValue("direction");
        entity.translateX(speed * tpf * direction.getValue());
    }
}
