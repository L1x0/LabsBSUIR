package by.astakhau.arkanoid.model.data.score;

public class ScoreTableManager implements ScoreService {
    private ScoreTable scoreTable;
    ScoreData data;

    private void init() {
        data = new ScoreData(new ScoreTableReader(), new ScoreTableWriter());
        scoreTable = data.readFile();
    }

    public ScoreTableManager() {
        init();
    }

    @Override
    public ScoreTable getScoreTable() {
        return scoreTable;
    }

    @Override
    public void writeScoreTable(ScoreTable scoreTable) {
        data.writeFile(scoreTable);
    }
}
