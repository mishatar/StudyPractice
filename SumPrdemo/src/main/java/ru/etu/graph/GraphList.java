package ru.etu.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphList implements Graph {

    // This implementation does not allow duplicates of vertices
    // Allows only one edge between any 2 vertices.
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
    public List<Edge> incidentEdges(Vertex vertex) throws InvalidVertexException {

        checkVertex(vertex);

        List<Edge> incidentEdges = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.contains(vertex)) {
                incidentEdges.add(edge);
            }
        }

        return incidentEdges;
    }

    @Override
    public Vertex opposite(Vertex vertex, Edge edge) throws InvalidVertexException, InvalidEdgeException {

        checkVertex(vertex);
        checkEdge(edge);

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
    public boolean areConnected(Vertex vertex1, Vertex vertex2) throws InvalidVertexException {
        checkVertex(vertex1);
        checkVertex(vertex2);

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
    public Vertex insertVertex(String element) throws InvalidVertexException {

        if (element == null) throw new IllegalArgumentException("Cannot create vertex with 'null' as name.");

        checkNoVertexWithData(element);

        Vertex newVertex = new Vertex(element);

        vertices.put(element, newVertex);

        return newVertex;
    }

    @Override
    public Edge insertEdge(Vertex vertex1, Vertex vertex2, int edgeElement) throws InvalidVertexException, InvalidEdgeException {

        checkVertex(vertex1);
        checkVertex(vertex2);

        if (vertex1.equals(vertex2)) {
            throw new InvalidVertexException("Cannot make loop edge.");
        }

        Edge newEdge = new Edge(edgeElement, vertex1, vertex2);

        checkEdgeDoesNotExists(newEdge);

        edges.add(newEdge);

        return newEdge;
    }

    @Override
    public Edge insertEdge(String vertElement1, String vertElement2, int edgeElement) throws InvalidVertexException, InvalidEdgeException {

        if (vertElement1 == null) throw new IllegalArgumentException("Cannot get vertex with 'null' as name.");
        if (vertElement2 == null) throw new IllegalArgumentException("Cannot get vertex with 'null' as name.");

        checkHasVertexWithData(vertElement1);
        checkHasVertexWithData(vertElement2);

        if (vertElement1.equals(vertElement2)) {
            throw new InvalidVertexException("Cannot make loop edge.");
        }

        Vertex outVertex = getVertex(vertElement1);
        Vertex inVertex = getVertex(vertElement2);

        Edge newEdge = new Edge(edgeElement, outVertex, inVertex);

        checkEdgeDoesNotExists(newEdge);

        edges.add(newEdge);

        return newEdge;
    }


    @Override
    public String removeVertex(Vertex vertex) throws InvalidVertexException {
        checkVertex(vertex);

        String element = vertex.getData();

        for (var edge : incidentEdges(vertex)) {
            edges.remove(edge);
        }

        vertices.remove(vertex.getData());

        return element;
    }

    @Override
    public int removeEdge(Edge edge) throws InvalidEdgeException {
        checkEdge(edge);

        int element = edge.getData();
        edges.remove(edge);

        return element;
    }

    @Override
    public Edge getEdge(Vertex vertexFrom, Vertex vertexTo) {
        checkVertex(vertexFrom);
        checkVertex(vertexTo);

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

        checkHasVertexWithData(vertexElFrom);
        checkHasVertexWithData(vertexElTo);

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

        checkHasVertexWithData(vertexEl);

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


    private void checkVertex(Vertex vertex) throws InvalidVertexException {
        if (vertex == null) throw new InvalidVertexException("Vertex is null.");

        if (!vertices.containsKey(vertex.getData())) {
            throw new InvalidVertexException("Vertex does not belong to this graph.");
        }
    }

    private void checkEdge(Edge edge) throws InvalidEdgeException {
        if (edge == null) throw new InvalidEdgeException("Edge is null.");

        for (var edgeObj : edges) {
            var edgeVal = edgeObj.getData();

            if (edgeVal == edge.getData() &&
                    ((edgeObj.getVertexInbound().getData().equals(edge.getVertexInbound().getData()) &&
                            edgeObj.getVertexOutbound().getData().equals(edge.getVertexOutbound().getData())) ||
                            (edgeObj.getVertexInbound().getData().equals(edge.getVertexOutbound().getData()) &&
                                    edgeObj.getVertexOutbound().getData().equals(edge.getVertexInbound().getData())))) {
                return;
            }
        }

        throw new InvalidEdgeException("Edge does not belong to this graph.");
    }

    private void checkNoVertexWithData(String data) throws InvalidVertexException {
        if (vertices.containsKey(data)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }
    }

    private void checkHasVertexWithData(String data) throws InvalidVertexException {
        if (!vertices.containsKey(data)) {
            throw new InvalidVertexException("No vertex contains " + data);
        }
    }

    private void checkEdgeDoesNotExists(Edge edge) throws InvalidEdgeException {

        for (var edgeObj : edges) {
            if ((edgeObj.getVertexInbound().equals(edge.getVertexInbound()) && edgeObj.getVertexOutbound().equals(edge.getVertexOutbound())) ||
                    (edgeObj.getVertexInbound().equals(edge.getVertexOutbound()) && edgeObj.getVertexOutbound().equals(edge.getVertexInbound()))) {
                throw new InvalidEdgeException("There's already a edge between these vertices.");
            }
        }
    }

    private void checkHasEdgeWithData(int data) throws InvalidEdgeException {

        for (var edgeObj : edges) {
            var edgeVal = edgeObj.getData();

            if (edgeVal == data) {
                return;
            }
        }

        throw new InvalidEdgeException("No edge contains " + data);

    }
}
