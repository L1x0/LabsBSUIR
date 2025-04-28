package by.astakhau.arkanoid.model.data;

import java.io.IOException;

public interface DataService<T> {
    T readFile() throws IOException;
    void writeFile(T data) throws IOException;
}
