package by.astakhau.arkanoid.model.data;

import java.io.IOException;
import java.util.List;

public interface DataReader<T> {
     List<T> readFile() throws IOException;
}
