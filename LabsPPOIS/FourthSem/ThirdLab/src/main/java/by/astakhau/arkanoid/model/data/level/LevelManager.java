package by.astakhau.arkanoid.model.data.level;

import by.astakhau.arkanoid.model.data.DataReader;
import by.astakhau.arkanoid.model.game.ArkanoidEntityFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelManager implements LevelService {
    List<Level> levels;

    public LevelManager() {
        init();
    }

    private void init() {
        levels = new ArrayList<>();
        loadLevels();
    }

    private void loadLevels() {
        DataReader<List<Level>> reader = new LevelReader();
        try {
            levels = reader.readFile();
        } catch (IOException e) {
            FXGL.getNotificationService().pushNotification("Уровни не найдены");
        }
    }

    @Override
    public Level getLevelById(int id) {
        if (levels.size() < id) throw new IllegalArgumentException("Level id out of bounds");
        else return levels.get(id);
    }

    @Override
    public void drawLevelById(int id) {
        ArkanoidEntityFactory factory = new ArkanoidEntityFactory();
        if (levels.isEmpty()) throw new IllegalArgumentException("Level id out of bounds");
        if (levels.size() < id) throw new IllegalArgumentException("Level id out of bounds");

        Level level = levels.get(id);

        for (int i = 0; i < level.getBricks().size(); i++) {
            Brick brick = level.getBricks().get(i);
            FXGL.getGameWorld().addEntity(factory.createBrick(new SpawnData(brick.xPos, brick.yPos), brick.health));
        }
    }
}
