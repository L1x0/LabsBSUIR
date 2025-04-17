package by.astakhau.arkanoid.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Level {
    List<Brick> bricks;
    int levelNumber;

    public void addBrick(Brick brick) {
        bricks.add(brick);
    }

    public void removeBrickByNum(int num) {
        if (bricks.size() < num) {
            throw new IllegalArgumentException("Not enough points");
        } else {
            bricks.remove(bricks.get(num));
        }
    }
}
