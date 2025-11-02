package by.astakhau.arkanoid.model.game.component;

import by.astakhau.arkanoid.Arkanoid;
import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.image.Image;
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
                ArkanoidEntityFactory arkanoidEntityFactory = new ArkanoidEntityFactory();

                var spawnData = new SpawnData(entity.getX(), entity.getY());
                entity.removeFromWorld();

                if (FXGL.random(0, 10) <= 3) {
                    FXGL.getGameWorld().addEntity(arkanoidEntityFactory.createBuff(spawnData));
                }
                return;
            }
            case 1 -> color = "brick_green.png";
            case 2 -> color = "brick_red.png";
            case 3 -> color = "brick_purple.png";
        }

        // Очистим старый view
        entity.getViewComponent().clearChildren();

        // Создаём ImageView из текстуры (FXGL.texture обычно возвращает ImageView)
        ImageView img = (ImageView) FXGL.texture(color);

        // Определяем желаемый размер (можно заменить на конкретное значение)
        double naturalW = 0;
        double naturalH = 0;
        Image image = img.getImage();
        if (image != null) {
            naturalW = image.getWidth();
            naturalH = image.getHeight();
        }

        // Выбираем целевой размер: если картинка маленькая — масштабируем до minSize, иначе используем её натуральный размер (или ограничим maxSize)
        double minSize = 16;   // минимальный диаметр
        double maxSize = 64;   // максимальный диаметр (по желанию)
        double size = Math.max(minSize, Math.min(maxSize, Math.max(naturalW, naturalH)));
        // Если image null (не подгрузилась), оставим size = 32
        if (size == 0) size = 32;

        img.setPreserveRatio(true);
        img.setFitWidth(size);           // масштабируем картинку по ширине
        // img.setFitHeight(size);       // не нужно, preserveRatio=true

        // Центрируем изображение в точке (0,0) сущности:
        img.setTranslateX(-size / 2.0);
        img.setTranslateY(-size / 2.0);

        // Добавляем в view
        entity.getViewComponent().addChild(img);

        // Сбрасываем старые хитбоксы и ставим круглый хитбокс радиусом size/2
        // Убедимся, что у сущности есть BoundingBoxComponent (обычно добавляется автоматически)
        entity.getBoundingBoxComponent().clearHitBoxes();
        entity.getBoundingBoxComponent().addHitBox(new HitBox("BODY", BoundingShape.circle(size / 1.0)));

        // при необходимости: убедиться, что entity помечена collidable (если не помечена ранее)
        // entity.setProperty("collidable", true); // обычно не нужно
    }

    public void reduceHealth() {
        health--;
        updateColor();

        Arkanoid.addPlayerScore();
    }
}