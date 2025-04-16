package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.entity.component.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BuffMovementComponent extends Component {
    @Setter
    private double speed;
    public BuffMovementComponent(double speed) {
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateY(speed * tpf);
    }
}

