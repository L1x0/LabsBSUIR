package by.astakhau.graphs;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Graph<T> implements AdjacencyMatrix<T> {
    private ArrayList<ArrayList<T>> adjacencyMatrix;

    T zero;
    T entity;

    public void setAdjacencyMatrix(ArrayList<ArrayList<T>> adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public Graph(T zero, T entity) {
        adjacencyMatrix = new ArrayList<>();
        adjacencyMatrix.add(new ArrayList<>());

        adjacencyMatrix.get(0).add(zero);
        
        this.zero = zero;
        this.entity = entity;
    }

    public Graph(Graph g, T zero, T entity) {
        this.adjacencyMatrix = g.getAdjacencyMatrix();
        this.zero = zero;
        this.entity = entity;
    }

    public ArrayList<ArrayList<T>> getAdjacencyMatrix() {
        return this.adjacencyMatrix;
    }

    @Override
    public boolean hasVertex() {
        return !this.adjacencyMatrix.get(0).isEmpty();
    }

    public boolean hasEdge(T firstVertex, T secondVertex) {
        if (!this.hasVertex()) {
            return false;
        } else {
            for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
                for (int j = 1; j < this.adjacencyMatrix.get(i).size(); j++) {
                    if (this.adjacencyMatrix.get(i).get(j).equals("1")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.hasVertex();
    }

    @Override
    public int getVertexCount() {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }
        return this.adjacencyMatrix.size() - 1;
    }

    @Override
    public int getEdgeCount() {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }

        int count = 0;

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            for (int j = 1; j < this.adjacencyMatrix.get(i).size(); j++) {
                if (this.adjacencyMatrix.get(i).get(j).equals("1")) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int getInVertexDegree(T name) {
        getVertexException(name);

        int count = 0;

        int index = this.adjacencyMatrix.get(0).indexOf(name);

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(i).get(index).equals("1")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getOutVertexDegree(T name) {
        getVertexException(name);

        int count = 0;
        int index = this.adjacencyMatrix.get(0).indexOf(name);

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(index).get(i).equals("1"))
                count++;
        }
        return count;
    }

    private void getVertexException(T name) {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }

        boolean found = false;

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(i).get(0).equals(name)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NoSuchElementException("Graph has no vertex " + name);
        }
    }

    @Override
    public void addVertex(T name) {
        this.adjacencyMatrix.add(new ArrayList<>());
        this.adjacencyMatrix.get(this.adjacencyMatrix.size() - 1).add(name);
        this.adjacencyMatrix.get(0).add(this.adjacencyMatrix.get(0).size(), name);


        for (int i = 1; i <= this.adjacencyMatrix.size() - 1; i++) {
            this.adjacencyMatrix.get(i).add(this.adjacencyMatrix.get(i).size(), zero);
        }

        while (this.adjacencyMatrix.get(adjacencyMatrix.size() - 1).size() != adjacencyMatrix.get(0).size()) {
            this.adjacencyMatrix
                    .get(adjacencyMatrix.size() - 1)
                    .add(zero);
        }
    }

    public void addVertex(T name, int index) {
        this.adjacencyMatrix.add(new ArrayList<>());
        this.adjacencyMatrix.get(index).add(name);
        this.adjacencyMatrix.get(0).add(index, name);


        for (int i = 1; i < this.adjacencyMatrix.size() - 1; i++) {
            this.adjacencyMatrix.get(i).add(index, zero);
        }

        while (this.adjacencyMatrix.get(index).size() < adjacencyMatrix.get(0).size()) {
            this.adjacencyMatrix
                    .get(index)
                    .add(index, zero);
        }
    }

    @Override
    public void addEdge(T FromVertex, T ToVertex) {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }
        getVertexException(FromVertex);
        getVertexException(ToVertex);

        int firstIndex = 0;
        int secondIndex = 0;

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(i).get(0).equals(FromVertex)) {
                firstIndex = i;
                break;
            }
        }

        for (int i = 1; i < this.adjacencyMatrix.get(0).size(); i++) {
            if (this.adjacencyMatrix.get(0).get(i).equals(ToVertex)) {
                secondIndex = i;
                break;
            }
        }

        this.adjacencyMatrix.get(firstIndex).set(secondIndex, entity);
    }

    @Override
    public void removeVertex(T name) {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }
        getVertexException(name);

        int index = this.adjacencyMatrix.get(0).indexOf(name);

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            this.adjacencyMatrix.get(i).remove(index);
        }

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(i).get(0).equals(name)) {
                this.adjacencyMatrix.remove(i);
                break;
            }
        }

        this.adjacencyMatrix.get(0).remove(adjacencyMatrix.get(0).indexOf(name));
    }

    @Override
    public void removeEdge(T FromVertex, T ToVertex) {
        if (!this.hasVertex()) {
            throw new NoSuchElementException("Graph has no vertex");
        }
        getVertexException(FromVertex);
        getVertexException(ToVertex);

        int firstIndex = 0;
        int secondIndex = 0;

        for (int i = 1; i < this.adjacencyMatrix.size(); i++) {
            if (this.adjacencyMatrix.get(i).get(0).equals(FromVertex)) {
                firstIndex = i;
                break;
            }
        }

        for (int i = 1; i < this.adjacencyMatrix.get(0).size(); i++) {
            if (this.adjacencyMatrix.get(0).get(i).equals(ToVertex)) {
                secondIndex = i;
                break;
            }
        }

        this.adjacencyMatrix.get(firstIndex).set(secondIndex, zero);
    }

    @Override
    public void clear() {
        this.adjacencyMatrix = null;
        this.adjacencyMatrix = new Graph(zero, entity).getAdjacencyMatrix();
    }

    @Override
    public VertexIterator getVertexIterator(int startPoint) {
        return new VertexIterator(startPoint, this);
    }

    @Override
    public EdgeIterator getEdgeIterator(int startX, int startY) {
        return new EdgeIterator(startX, startY, this);
    }

    @Override
    public ConstEdgeIterator getConstEdgeIterator(int startX, int startY) {
        return new ConstEdgeIterator(startX, startY, this);
    }

    @Override
    public ConstVertexIterator getConstVertexIterator(int startPoint) {
        return new ConstVertexIterator(startPoint, this);
    }

    public IteratorOverIncidentVertices getIteratorOverIncidentVertices(T name) {
        return new IteratorOverIncidentVertices(name, this);
    }
}