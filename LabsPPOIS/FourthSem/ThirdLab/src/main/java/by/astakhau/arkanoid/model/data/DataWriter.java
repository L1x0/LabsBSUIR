package by.astakhau.arkanoid.model.data;

import java.io.IOException;
import java.util.List;

public interface DataWriter<T> {
    void writeData(List<T> records) throws IOException;
}
