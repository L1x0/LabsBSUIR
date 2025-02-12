package by.astakhau.graphs;


import java.util.ArrayList;
import java.util.NoSuchElementException;

public class EdgeIterator<T>  {
    Graph graph;
    private ArrayList<ArrayList<T>> adjacencyMatrix;
    int actualX, actualY;

    public EdgeIterator(int startX, int startY, Graph graph) {
        if (startX <= 0 || startY <= 0) {
            throw new IllegalArgumentException("startX and startY must be positive");
        }

        this.graph = graph;
        this.adjacencyMatrix = graph.getAdjacencyMatrix();

        boolean found = false;
        for (actualX = startX; actualX < adjacencyMatrix.size() && !found; actualX++) {
            for (actualY = startY; actualY < adjacencyMatrix.size(); actualY++) {
                if (adjacencyMatrix.get(actualX).get(actualY).equals("1")) {
                    found = true;
                    break;
                }
            }

        }
    }

    public EdgeIterator(Graph graph) {
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
        boolean found = false;

        for (int i = actualX; i < adjacencyMatrix.size() && !found; i++) {
            for (int j = actualY; j < adjacencyMatrix.get(i).size(); j++) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){
                    found = true;

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

        for (int i = actualX; i >= 1; i--) {
            for (int j = actualY; j >= 1; j--) {
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
        boolean found = false;

        for (int i = actualX; i >= 0 && !found; i--) {
            for (int j = actualY; j >= 0; j--) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")){

                    actualX = i;
                    actualY = j;
                    found = true;

                    break;
                }
            }
        }

        return this.adjacencyMatrix.get(actualX).get(actualY);
    }

    public void remove() {
        this.adjacencyMatrix.get(actualX).set(actualY, null);

        graph.setAdjacencyMatrix(this.adjacencyMatrix);
    }

    
    public void set(T s) {
        this.adjacencyMatrix.get(actualX).set(actualY, s);
        graph.setAdjacencyMatrix(this.adjacencyMatrix);
    }

}
