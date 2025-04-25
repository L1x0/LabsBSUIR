package by.astakhau.arkanoid.model.data;

import java.io.IOException;

public interface DataReader<T> {
      T readFile() throws IOException;
}
