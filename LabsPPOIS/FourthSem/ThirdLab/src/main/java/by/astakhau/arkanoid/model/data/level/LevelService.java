package by.astakhau.arkanoid.model.data.level;

public interface LevelService {
    Level getLevelById(int id);
    void drawLevelById(int id);
}
