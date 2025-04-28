package by.astakhau.arkanoid.model.data.score;

public interface ScoreService {
    ScoreTable getScoreTable();
    void writeScoreTable(ScoreTable scoreTable);
}
