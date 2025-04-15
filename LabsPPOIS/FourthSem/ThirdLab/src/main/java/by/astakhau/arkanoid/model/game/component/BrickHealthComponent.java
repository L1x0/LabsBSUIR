package by.astakhau.arkanoid.model.game.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;


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
        String color = "";

        switch (health) {
            case 0 -> {
                entity.removeFromWorld();
                return;
            }
            case 1 -> color = "brick_green.png";    // Синий
            case 2 -> color = "brick_red.png";   // Оранжевый
            case 3 -> color = "brick_purple.png";  // Красный
        }
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(FXGL.texture(color));
    }

    public void reduceHealth() {
        health--;
        updateColor();
    }
}