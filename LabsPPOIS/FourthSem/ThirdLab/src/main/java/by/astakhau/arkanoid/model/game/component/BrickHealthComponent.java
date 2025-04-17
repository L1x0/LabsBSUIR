package by.astakhau.arkanoid.model.game.component;

import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;


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
                var spawnData = new SpawnData(entity.getX(),entity.getY());
                entity.removeFromWorld();

                if (FXGL.random(0, 10) <= 3) {
                    ArkanoidEntityFactory arkanoidEntityFactory = new ArkanoidEntityFactory();
                    FXGL.getGameWorld().addEntity(arkanoidEntityFactory.createBuff(spawnData));
                }
                return;
            }
            case 1 -> color = "brick_green.png";
            case 2 -> color = "brick_red.png";
            case 3 -> color = "brick_purple.png";
        }
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(FXGL.texture(color));
    }

    public void reduceHealth() {
        health--;
        updateColor();
    }
}