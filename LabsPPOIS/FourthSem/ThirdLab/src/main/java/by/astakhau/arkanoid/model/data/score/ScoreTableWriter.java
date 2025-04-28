package by.astakhau.arkanoid.model.data.score;

import by.astakhau.arkanoid.model.data.DataWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;


public class ScoreTableWriter implements DataWriter<ScoreTable> {
    private static final String STATE_FILE = "score-table.json";

    @Override
    public void writeData(ScoreTable record) throws IOException {
        File f = new File(STATE_FILE);
        if (f.exists()) {
            if (!f.delete()) {
                throw new IOException("Не удалось удалить старый файл " + STATE_FILE);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.writeValue(new File(STATE_FILE), record);
    }
}
