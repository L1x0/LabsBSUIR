package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import lombok.Getter;

public class BallMovementComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
        physics.setAngularVelocity(0);

        Vec2 velocity = physics.getBody().getLinearVelocity();
        boolean needAdjust = false;

        float minAxisSpeed = 3;
        if (Math.abs(velocity.x) < minAxisSpeed) {
            velocity.x = minAxisSpeed * Math.signum(velocity.x);
            needAdjust = true;
        }

        if (Math.abs(velocity.y) < minAxisSpeed) {
            velocity.y = minAxisSpeed * Math.signum(velocity.y);
            needAdjust = true;
        }

        float maxAxisSpeed = 15;
        if (Math.abs(velocity.x) > maxAxisSpeed) {
            velocity.x = maxAxisSpeed * Math.signum(velocity.x);
            needAdjust = true;
        }

        if (Math.abs(velocity.y) > maxAxisSpeed) {
            velocity.y = maxAxisSpeed * Math.signum(velocity.y);
            needAdjust = true;
        }

        if (needAdjust) {
            float targetSpeed = 10;
            velocity = velocity.normalize().mul(targetSpeed);
        }

        physics.getBody().setLinearVelocity(velocity);
    }
}