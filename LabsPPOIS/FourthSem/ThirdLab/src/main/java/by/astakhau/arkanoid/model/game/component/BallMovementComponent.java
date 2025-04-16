package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.util.Duration;

public class BallMovementComponent extends Component {
    float minAxisSpeed = 3;
    float targetSpeed = 10;
    float maxAxisSpeed = 15;

    @Override
    public void onUpdate(double tpf) {
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);

        physics.setAngularVelocity(0);

        Vec2 velocity = physics.getBody().getLinearVelocity();

        boolean needAdjust = false;

        if (Math.abs(velocity.x) < minAxisSpeed) {
            velocity.x = minAxisSpeed * Math.signum(velocity.x);
            needAdjust = true;
        }

        if (Math.abs(velocity.y) < minAxisSpeed) {
            velocity.y = minAxisSpeed * Math.signum(velocity.y);
            needAdjust = true;
        }

        if (Math.abs(velocity.y) > maxAxisSpeed) {
            velocity.y = maxAxisSpeed;
            needAdjust = true;
        }

        if (Math.abs(velocity.x) > maxAxisSpeed) {
            velocity.x = maxAxisSpeed;
            needAdjust = true;
        }

        if (needAdjust) {
            velocity = velocity.normalize().mul(targetSpeed);
        }

        physics.getBody().setLinearVelocity(velocity);
    }

    public void timeFreeze() {
        minAxisSpeed = 2;
        targetSpeed = 4;
        maxAxisSpeed = 7;

        FXGL.runOnce(() -> {
            minAxisSpeed = 3;
            targetSpeed = 10;
            maxAxisSpeed = 15;
        }, Duration.seconds(8));
    }
}
