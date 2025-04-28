package by.astakhau.arkanoid.model.data;

import java.io.IOException;
import java.util.List;

public interface DataWriter<T> {
    void writeData(T record) throws IOException;
}
