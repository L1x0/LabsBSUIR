package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BrickHealthComponent extends Component {
    private int health;

    public BrickHealthComponent(int health) {
        this.health = health;
    }

    @Override
    public void onAdded() {
        updateColor();
    }

    private void updateColor() {
        switch (health) {
            case 0:  {
                entity.removeFromWorld();
                break;
            }
            case 1:  {
                Rectangle BBox = (Rectangle) entity.getViewComponent().getChildren().get(0);
                BBox.setFill(Color.GREEN);
                break;
            }
            case 2:  {
                Rectangle BBox = (Rectangle) entity.getViewComponent().getChildren().get(0);
                BBox.setFill(Color.RED);
                break;
            }
            case 3:  {
                Rectangle BBox = (Rectangle) entity.getViewComponent().getChildren().get(0);
                BBox.setFill(Color.PURPLE);
                break;
            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        updateColor();
    }

    public void reduceHealth() {
        health--;
    }
}
