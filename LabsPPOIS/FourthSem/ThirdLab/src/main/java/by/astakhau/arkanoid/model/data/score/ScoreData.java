package by.astakhau.arkanoid.model.data.score;

import by.astakhau.arkanoid.model.data.DataReader;
import by.astakhau.arkanoid.model.data.DataService;
import by.astakhau.arkanoid.model.data.DataWriter;
import com.almasb.fxgl.dsl.FXGL;

import java.io.IOException;
import java.util.List;

public class ScoreData implements DataService<ScoreTable> {
    private DataReader<ScoreTable> reader;
    private DataWriter<ScoreTable> writer;

    public ScoreData(DataReader<ScoreTable> reader, DataWriter<ScoreTable> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public ScoreTable readFile() {
        ScoreTable scoreTable;
        try {
            scoreTable = reader.readFile();
        } catch (IOException e) {
            FXGL.getNotificationService().pushNotification("Чтение таблицы лидеров не удалось");
            scoreTable = new ScoreTable(List.of());
        }
        return scoreTable;
    }

    @Override
    public void writeFile(ScoreTable data) {
        try {
            writer.writeData(data);
        } catch (IOException e) {
            FXGL.getNotificationService().pushNotification("не удалось записать таблицу");
        }
    }
}
