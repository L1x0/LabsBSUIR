package by.astakhau.graphs;

public interface AdjacencyMatrix<T> {
    boolean hasVertex();
    boolean hasEdge(T firstVertex, T secondVertex);
    boolean isEmpty();

    int getVertexCount();
    int getEdgeCount();
    int getInVertexDegree(T name);
    int getOutVertexDegree(T name);

    void addVertex(T name);
    void addEdge(T FromVertex, T ToVertex);
    void removeVertex(T name);
    void removeEdge(T FromVertex, T ToVertex);
    void clear();

    VertexIterator getVertexIterator(int startPoint);
    EdgeIterator getEdgeIterator(int startX, int startY);
    ConstEdgeIterator getConstEdgeIterator(int startX, int startY);
    ConstVertexIterator getConstVertexIterator(int startPoint);
}
