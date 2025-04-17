package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.entity.component.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BuffMovementComponent extends Component {
    private double speed;
    private double absolutSpeed;
    public BuffMovementComponent(double speed) {
        this.speed = speed;
        this.absolutSpeed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateY(speed * tpf);
    }
}

