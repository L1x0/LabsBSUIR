package by.astakhau.graphs;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class IteratorOverIncidentVertices<T> {
    private int actualIndex;
    ArrayList<Integer> coords;
    Graph graph;
    private ArrayList<ArrayList<T>> adjacencyMatrix;

    public IteratorOverIncidentVertices(T name, Graph graph) {
        this.graph = graph;
        adjacencyMatrix = graph.getAdjacencyMatrix();

        actualIndex = this.adjacencyMatrix.get(0).indexOf(name);
        coords = this.findAllNeighbours();
        actualIndex = 0;
    }

    private ArrayList<Integer> findAllNeighbours() {
        ArrayList<Integer> neighbours = new ArrayList<>();

        for (int i = 0; i < adjacencyMatrix.get(this.actualIndex).size(); i++) {
            if (this.adjacencyMatrix.get(this.actualIndex).get(i).equals("1")) {
                if (!neighbours.contains(i)) {
                    neighbours.add(i);
                }
            }
        }

        return neighbours;
    }

    public boolean hasNext() {
        return actualIndex + 1 <= this.coords.size() - 1;
    }

    public T next() {
        if (!hasNext()) { throw new NoSuchElementException(); }

        actualIndex++;
        return adjacencyMatrix.get(0).get(coords.get(actualIndex));
    }

    public boolean hasPrevious() {
        return actualIndex - 1 >= 0;
    }

    public T previous() {
        if (!hasPrevious()) { throw new NoSuchElementException(); }

        actualIndex--;
        return adjacencyMatrix.get(0).get(coords.get(actualIndex));
    }
}
