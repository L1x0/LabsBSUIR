package by.astakhau.tests;

import by.astakhau.graphs.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    public void addVertexTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        assertEquals(1, graph.getVertexCount());

        graph.addVertex("B");
        assertEquals(2, graph.getVertexCount());

        graph.addVertex("C", 1);
        assertEquals(3, graph.getVertexCount());
        assertEquals("C", graph.getAdjacencyMatrix().get(0).get(1));
    }

    @Test
    public void addEdgeTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");
        assertEquals(1, graph.getEdgeCount());

        assertEquals("1", graph.getAdjacencyMatrix().get(1).get(2));
    }

    @Test
    public void hasEdgeTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");
        assertEquals(1, graph.getEdgeCount());

        assertTrue(graph.hasEdge("A", "B"));
    }

    @Test
    public void hasVertexTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");
        assertEquals(1, graph.getEdgeCount());

        assertTrue(graph.hasVertex());
    }

    @Test
    public void isEmptyTest() {
        Graph<String> graph = new Graph("0", "1");

        assertTrue(graph.isEmpty());
    }

    @Test
    public void getInVertexDegreeTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");
        assertEquals(1, graph.getInVertexDegree("B"));
    }

    @Test
    public void getOutVertexDegreeTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");
        assertEquals(1, graph.getOutVertexDegree("A"));
    }

    @Test
    public void removeVertexTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");

        graph.removeVertex("A");
        assertEquals(1, graph.getVertexCount());
        assertEquals(0, graph.getEdgeCount());
    }

    @Test
    public void removeEdgeTest() {
        Graph<String> graph = new Graph("0", "1");

        graph.addVertex("A");
        graph.addVertex("B");

        graph.addEdge("A", "B");

        graph.removeEdge("A", "B");
        assertEquals(0, graph.getEdgeCount());
    }

    @Test
    public void clearTest() {
        Graph<String> graph = new Graph("0", "1");
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");

        graph.clear();

        assertEquals(0, graph.getVertexCount());
        assertEquals(0, graph.getEdgeCount());
    }

    @Test
    public void getIteratorsTest() {
        Graph<String> graph = new Graph("0", "1");
        graph.addVertex("A");

        assertEquals(VertexIterator.class, graph.getVertexIterator(1).getClass());
        assertEquals(ConstVertexIterator.class, graph.getConstVertexIterator(1).getClass());
        assertEquals(EdgeIterator.class, graph.getEdgeIterator(1, 1).getClass());
        assertEquals(ConstEdgeIterator.class, graph.getConstEdgeIterator(1, 1).getClass());
        assertEquals(IteratorOverIncidentVertices.class,
                graph.getIteratorOverIncidentVertices("A").getClass());
    }
}
