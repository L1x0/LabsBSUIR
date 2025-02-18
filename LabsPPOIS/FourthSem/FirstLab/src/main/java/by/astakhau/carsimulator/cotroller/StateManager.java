package by.astakhau.carsimulator.cotroller;


import by.astakhau.carsimulator.model.Driver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class StateManager {
    private static final String STATE_FILE = "driver_state.json";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void saveState(Driver driver) {
        try {
            MAPPER.writeValue(new File(STATE_FILE), driver);
        } catch (IOException e) {
            System.err.println("не сохраняет состояние");
        }
    }

    public static Driver loadState() {
        File file = new File(STATE_FILE);
        if (file.exists()) {
            try {
                Driver driver = MAPPER.readValue(file, Driver.class);

                return driver;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ошибка загрузки");
            }
        } else {
            System.out.println("Файл не найден");
        }
        return null;
    }

    public static void clearState() {
        File file = new File(STATE_FILE);
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Не удаляется");
            }
        } else {
            System.err.println("Не находит для удаления");
        }
    }
}

