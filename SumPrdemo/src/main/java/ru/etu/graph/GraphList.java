package ru.etu.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphList implements Graph {

    // не допускает дублирования вершин.
    // Разрешается только одно ребро между любыми двумя вершинами.
    private final Map<String, Vertex> vertices;
    private final List<Edge> edges;

    public GraphList() {
        vertices = new HashMap<>();
        edges = new ArrayList<>();
    }

    @Override
    public int verticesNum() {
        return vertices.size();
    }

    @Override
    public int edgesNum() {
        return edges.size();
    }

    @Override
    public List<Vertex> getVertices() {
        return new ArrayList<>(vertices.values());
    }

    @Override
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    @Override
    public List<Edge> incidentEdges(Vertex vertex) {

        List<Edge> incidentEdges = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.contains(vertex)) {
                incidentEdges.add(edge);
            }
        }

        return incidentEdges;
    }

    @Override
    public Vertex opposite(Vertex vertex, Edge edge) {


        if (!edge.contains(vertex)) {
            return null;
        }

        if (edge.getVertices().getKey().equals(vertex)) {
            return edge.getVertices().getValue();
        } else {
            return edge.getVertices().getKey();
        }
    }

    @Override
    public boolean areConnected(Vertex vertex1, Vertex vertex2) {

        if (vertex1.equals(vertex2)) {
            return false;
        }

        for (Edge edge : edges) {
            if (edge.contains(vertex2) && edge.contains(vertex1)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Vertex insertVertex(String element) {

        if (element == null) throw new IllegalArgumentException("Cannot create vertex with 'null' as name.");


        Vertex newVertex = new Vertex(element);

        vertices.put(element, newVertex);

        return newVertex;
    }

    @Override
    public Edge insertEdge(Vertex vertex1, Vertex vertex2, int edgeElement) {

        Edge newEdge = new Edge(edgeElement, vertex1, vertex2);

        edges.add(newEdge);

        return newEdge;
    }

    @Override
    public Edge insertEdge(String vertElement1, String vertElement2, int edgeElement) {

        if (vertElement1 == null) throw new IllegalArgumentException("Cannot get vertex with 'null' as name.");
        if (vertElement2 == null) throw new IllegalArgumentException("Cannot get vertex with 'null' as name.");

        Vertex outVertex = getVertex(vertElement1);
        Vertex inVertex = getVertex(vertElement2);

        Edge newEdge = new Edge(edgeElement, outVertex, inVertex);


        edges.add(newEdge);

        return newEdge;
    }


    @Override
    public String removeVertex(Vertex vertex) {

        String element = vertex.getData();

        for (var edge : incidentEdges(vertex)) {
            edges.remove(edge);
        }

        vertices.remove(vertex.getData());

        return element;
    }

    @Override
    public int removeEdge(Edge edge) {

        int element = edge.getData();
        edges.remove(edge);

        return element;
    }

    @Override
    public Edge getEdge(Vertex vertexFrom, Vertex vertexTo) {

        for (var edge : edges) {
            if (edge.contains(vertexFrom) && edge.contains(vertexTo)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public Edge getEdge(String vertexElFrom, String vertexElTo) {
        if (vertexElFrom == null || vertexElTo == null)
            throw new IllegalArgumentException("Vertex name cannot be null.");

        var vertexFrom = getVertex(vertexElFrom);
        var vertexTo = getVertex(vertexElTo);

        for (var edge : edges) {
            if (edge.contains(vertexFrom) && edge.contains(vertexTo)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public Vertex getVertex(String vertexEl) {
        if (vertexEl == null)
            throw new IllegalArgumentException("Vertex name cannot be null.");


        for (Vertex vertex : vertices.values()) {
            if (vertex.getData().equals(vertexEl)) {
                return vertex;
            }
        }
        return null;
    }

    @Override
    public String toString() {

        var builder = new StringBuilder();

        builder.append("Graph (not oriented) with ").append(verticesNum())
                .append(" vertices and ").append(edgesNum()).append(" edges:\n");

        builder.append("Vertices: \n");
        for (var vertex : vertices.values()) {
            builder.append("\t").append(vertex.toString()).append("\n");
        }

        builder.append("Edges: \n");
        for (var edge : edges) {
            builder.append("\t").append(edge.toString()).append("\n");
        }


        return builder.toString();
    }
}