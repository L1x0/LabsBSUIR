package by.astakhau.arkanoid.model.data;

public interface LevelService {
    Level getLevelById(int id);
    void drawLevelById(int id);
}
