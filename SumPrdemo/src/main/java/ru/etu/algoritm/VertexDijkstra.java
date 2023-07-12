package ru.etu.algoritm;

import ru.etu.graph.Vertex;

import java.util.Objects;

/**
 * Extended version of Vertex for Dijkstra algorithm with "parent" reference
 */
public class VertexDijkstra extends Vertex {

    private VertexDijkstra parent;

    private final Vertex original;

    /**
     * Constructor
     *
     * @param vertex original vertex
     */
    public VertexDijkstra(Vertex vertex) {
        super(vertex.getData());
        original = vertex;
    }

    /**
     * Add parent vertex (vertex with shortest way from beginning)
     *
     * @param parent parent vertex
     */
    public void setParent(VertexDijkstra parent) {
        this.parent = parent;
    }

    /**
     * Get parent vertex (vertex with shortest way from beginning)
     *
     * @return parent vertex
     */
    public VertexDijkstra getParent() {
        return parent;
    }

    /**
     * Returns original Vertex from which this one was created
     *
     * @return original Vertex from which this one was created
     */
    public Vertex getOriginal() {
        return original;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Vertex.class) return o.equals(this);
        if (!(o instanceof VertexDijkstra)) return false;
        //System.out.println(((VertexDijkstra) o).getElement()+" and "+this.getElement()+" = "+Objects.equals(((VertexDijkstra) o).getElement(), this.getElement()));
        return Objects.equals(((VertexDijkstra) o).getData(), this.getData());
    }
}
