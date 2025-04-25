package by.astakhau.arkanoid.model.data.config;

import by.astakhau.arkanoid.model.data.DataReader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class ConfigReader implements DataReader<AppConfig> {

    @Override
    public AppConfig readFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        InputStream is = getClass().getResourceAsStream("/settings.json");
        return mapper.readValue(is, new TypeReference<AppConfig>() {});
    }
}
