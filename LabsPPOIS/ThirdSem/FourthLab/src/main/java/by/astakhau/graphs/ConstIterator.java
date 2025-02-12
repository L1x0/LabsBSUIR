package by.astakhau.graphs;

public interface ConstIterator<T> {
    boolean hasNext();
    T next();

    boolean hasPrevious();
    T previous();
}
