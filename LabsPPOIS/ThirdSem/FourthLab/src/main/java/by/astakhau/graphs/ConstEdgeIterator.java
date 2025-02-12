package by.astakhau.graphs;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ConstEdgeIterator<T> {

    Graph graph;
    private ArrayList<ArrayList<T>> adjacencyMatrix;
    int actualX = 1, actualY = 1;

    public ConstEdgeIterator(int startX, int startY, Graph graph) {
        if (startX <= 0 || startY <= 0) {
            throw new IllegalArgumentException("startX and startY must be non-negative");
        }

        this.actualX = startX;
        this.actualY = startY;
        this.graph = graph;
        this.adjacencyMatrix = graph.getAdjacencyMatrix();
    }

    public ConstEdgeIterator(Graph graph) {
        this.graph = graph;
        this.adjacencyMatrix = graph.getAdjacencyMatrix();
    }

    public boolean hasNext() {
        boolean hasNext = false;

        for (int i = actualX; i < adjacencyMatrix.size(); i++) {
            for (int j = actualY; j < adjacencyMatrix.get(i).size(); j++) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){
                    hasNext = true;

                    break;
                }
            }
        }

        return hasNext;
    }


    public T next() {
        if (!hasNext()) throw new NoSuchElementException();

        for (int i = actualX; i < adjacencyMatrix.size(); i++) {
            for (int j = actualY; j < adjacencyMatrix.get(i).size(); j++) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){

                    actualX = i;
                    actualY = j;

                    break;
                }
            }
        }

        return this.adjacencyMatrix.get(actualX).get(actualY);
    }


    public boolean hasPrevious() {
        boolean hasPrevios = false;

        for (int i = actualX; i >= 0; i--) {
            for (int j = actualY; j >= 0; j--) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){
                    hasPrevios = true;

                    break;
                }
            }
        }

        return hasPrevios;
    }


    public T previous() {
        if (!hasPrevious()) throw new NoSuchElementException();

        for (int i = actualX; i >= 0; i--) {
            for (int j = actualY; j >= 0; j--) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){

                    actualX = i;
                    actualY = j;

                    break;
                }
            }
        }

        return this.adjacencyMatrix.get(actualX).get(actualY);
    }
}
