package by.astakhau.arkanoid.model.data.score;

import by.astakhau.arkanoid.model.data.DataReader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;


public class ScoreTableReader implements DataReader<ScoreTable> {
    private static final String STATE_FILE = "score-table.json";
    @Override
    public ScoreTable readFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(STATE_FILE), ScoreTable.class);
    }
}
