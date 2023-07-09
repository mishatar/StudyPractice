package ru.etu.graph;

import java.util.*;


public class DirectedGraphList implements DirectedGraph {

    // не допускает дублирования вершин.
    // Допускается максимум два ребра между любыми двумя вершинами. Но они должны быть разнонаправленными.
    private final Map<String, Vertex> vertices;
    private final List<Edge> edges;

    public DirectedGraphList() {
        this.vertices = new HashMap<>();
        this.edges = new ArrayList<>();
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
    public List<Edge> incidentEdges(Vertex vertexFrom) {

        List<Edge> incidentEdges = new ArrayList<>();

        for (var edge : edges) {
            if (edge.getVertexOutbound().equals(vertexFrom)) {
                incidentEdges.add(edge);
            }
        }

        return incidentEdges;
    }

    @Override
    public List<Edge> inboundEdges(Vertex vertexTo) {

        List<Edge> incidentEdges = new ArrayList<>();
        for (var edge : edges) {

            if (edge.getVertexInbound().equals(vertexTo)) {
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
    public boolean areConnected(Vertex vertexOut, Vertex vertexIn) {

        if (vertexOut.equals(vertexIn)) {
            return false;
        }

        for (var edge : edges) {
            if (edge.getVertexOutbound().equals(vertexOut) && edge.getVertexInbound().equals(vertexIn)) {
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
    public Edge insertEdge(Vertex vertexOut, Vertex vertexIn, int edgeElement) {

        Edge newEdge = new Edge(edgeElement, vertexOut, vertexIn);


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

        //удалить инцидентные ребра (входящие и исходящие)
        Collection<Edge> inOutEdges = incidentEdges(vertex);
        inOutEdges.addAll(inboundEdges(vertex));

        for (var edge : inOutEdges) {
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
            if (edge.getVertexOutbound().equals(vertexFrom) && edge.getVertexInbound().equals(vertexTo)) {
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
            if (edge.getVertexOutbound().equals(vertexFrom) && edge.getVertexInbound().equals(vertexTo)) {
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

        builder.append("Graph (oriented) with ").append(verticesNum())
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
