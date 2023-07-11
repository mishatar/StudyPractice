package ru.etu.graph;

import javafx.util.Pair;

import java.util.Objects;

/**
 * Class of Edge
 *
 * @see Vertex
 */
public class Edge {

    private int data;
    private final Vertex vertexOutbound;
    private final Vertex vertexInbound;

    public Edge(int data, Vertex vertexOutbound, Vertex vertexInbound) {
        this.data = data;
        this.vertexOutbound = vertexOutbound;
        this.vertexInbound = vertexInbound;
    }

    public boolean contains(Vertex vertex) {
        return Objects.equals(vertexOutbound.getData(), vertex.getData()) || Objects.equals(vertexInbound.getData(), vertex.getData());
    }

    /**
     * Returns the pair of vertices (from and to)
     *
     * @return pair of vertices
     */
    public Pair<Vertex, Vertex> getVertices() {
        return new Pair<>(vertexOutbound, vertexInbound);
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public Vertex getVertexOutbound() {
        return vertexOutbound;
    }

    public Vertex getVertexInbound() {
        return vertexInbound;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "element=" + data +
                ", vertexOutbound=" + vertexOutbound +
                ", vertexInbound=" + vertexInbound +
                '}';
    }
}
