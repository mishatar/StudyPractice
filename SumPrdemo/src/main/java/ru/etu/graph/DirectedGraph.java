package ru.etu.graph;

import java.util.List;

/**
 * Directed Graph is a set of vertices connected by edges. Edges have direction.
 *
 * @see Graph
 * @see Edge
 * @see Vertex
 */
public interface DirectedGraph extends Graph {

    /**
     * Finds and returns list of incident edges that start from <code>vertexFrom</code>
     *
     * @param vertexFrom vertex from which to find incident edges that directed from it.
     * @return list of incident edges that directed from <code>vertexFrom</code>
     * @throws InvalidVertexException if vertex is invalid or does not belong to the graph
     */
    @Override
    List<Edge> incidentEdges(Vertex vertexFrom) throws InvalidVertexException;

    /**
     * Finds and returns list of incident edges that finish in <code>vertexTo</code>
     *
     * @param vertexTo vertex from which to find incident edges that finish in it
     * @return list of incident edges that finish in <code>vertexTo</code>
     * @throws InvalidVertexException if vertex is invalid or does not belong to the graph
     */
    List<Edge> inboundEdges(Vertex vertexTo) throws InvalidVertexException;

    /**
     * Determines if two vertices are connected with any edge
     *
     * @param vertexOut start vertex
     * @param vertexIn  finish vertex
     * @return true if connected, false - otherwise
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     */
    @Override
    boolean areConnected(Vertex vertexOut, Vertex vertexIn) throws InvalidEdgeException;

    /**
     * Inserts new edge to the graph
     *
     * @param vertexOut   start vertex link
     * @param vertexIn    finish vertex link
     * @param edgeElement data to be stored in the edge
     * @return link to newly created edge
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     * @throws InvalidEdgeException   if there is already an edge with the same data and vertices
     */
    @Override
    Edge insertEdge(Vertex vertexOut, Vertex vertexIn, int edgeElement) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Inserts new edge to the graph
     *
     * @param vertElement1 start vertex data
     * @param vertElement2 finish vertex data
     * @param edgeElement  data to be stored in the edge
     * @return link to newly created edge
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     * @throws InvalidEdgeException   if there is already an edge with the same data and vertices
     */
    @Override
    Edge insertEdge(String vertElement1, String vertElement2, int edgeElement) throws InvalidVertexException, InvalidEdgeException;
}
