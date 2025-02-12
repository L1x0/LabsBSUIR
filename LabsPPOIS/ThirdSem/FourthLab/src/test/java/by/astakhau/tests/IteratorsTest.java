package by.astakhau.tests;

import by.astakhau.graphs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IteratorsTest {
    private static Graph<String> graph;
    private static VertexIterator vertexIterator;
    private static EdgeIterator edgeIterator;
    private static ConstEdgeIterator constEdgeIterator;
    private static ConstVertexIterator constVertexIterator;
    private static IteratorOverIncidentVertices iteratorOverIncidentVertices;

    @BeforeEach
    public void setUp() {
        graph = new Graph<>("0", "1");
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");
        graph.addEdge("D", "A");

        vertexIterator = graph.getVertexIterator(1);
        edgeIterator = graph.getEdgeIterator(1, 1);
        constVertexIterator = graph.getConstVertexIterator(1);
        constEdgeIterator = graph.getConstEdgeIterator(1, 1);
        iteratorOverIncidentVertices =
                graph.getIteratorOverIncidentVertices("A");
    }

    @Test
    public void hasNextTest() {
        assertTrue(iteratorOverIncidentVertices.hasNext());
        assertTrue(vertexIterator.hasNext());
        assertTrue(edgeIterator.hasNext());
        assertTrue(constEdgeIterator.hasNext());
        assertTrue(constVertexIterator.hasNext());
    }

    @Test
    public void nextTest() {
        assertEquals("B", vertexIterator.next());
        assertEquals("B", constVertexIterator.next());
        assertEquals("1", constEdgeIterator.next());
        assertEquals("1", edgeIterator.next());

    }

    @Test
    public void hasPreviousTest() {
        assertFalse(iteratorOverIncidentVertices.hasPrevious());
        assertFalse(vertexIterator.hasPrevious());
        assertTrue(edgeIterator.hasPrevious());
        assertFalse(constEdgeIterator.hasPrevious());
        assertFalse(constVertexIterator.hasPrevious());
    }

    @Test
    public void PreviousTest() {
        vertexIterator.next();
        constVertexIterator.next();
        edgeIterator.next();
        constEdgeIterator.next();
        iteratorOverIncidentVertices.next();

        assertEquals("A", vertexIterator.previous());
        assertEquals("A", constVertexIterator.previous());
        assertEquals("1", edgeIterator.previous());
        assertEquals("1", constEdgeIterator.previous());
        assertEquals("B", iteratorOverIncidentVertices.previous());
    }

    @Test
    public void removeTest() {
        vertexIterator.remove();
        assertEquals(4, graph.getVertexCount());
    }

    @Test
    public void addTest() {
        vertexIterator.add("bimbimbombom");
        assertEquals(6, graph.getVertexCount());
    }
}
