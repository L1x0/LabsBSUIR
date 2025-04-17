package by.astakhau.arkanoid.model.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class LevelReader implements DataReader<Level> {

    @Override
    public List<Level> readFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        InputStream is = getClass().getResourceAsStream("/levels.json");
        return mapper.readValue(is, new TypeReference<List<Level>>() {
        });
    }
}
