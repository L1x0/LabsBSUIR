package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.entity.component.Component;

public class OutOfBoundsComponent extends Component {
    @Override
    public void onUpdate(double tpf) {
        if (entity.getY() < -15)
            entity.removeFromWorld();
    }
}
