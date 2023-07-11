package ru.etu.graph;

import java.util.List;

/**
 * Graph is a set of vertices connected by edges. Edges have no direction.
 *
 * @see Edge
 * @see Vertex
 */
public interface Graph {

    /**
     * Method that returns number of vertices in the graph
     *
     * @return number of vertices in the graph
     */
    int verticesNum();

    /**
     * Method that returns number of edges in the graph
     *
     * @return number of edges in the graph
     */
    int edgesNum();

    /**
     * Method that returns list of vertices
     *
     * @return list of vertices
     */
    List<Vertex> getVertices();

    /**
     * Method that returns list of edges
     *
     * @return list of edges
     */
    List<Edge> getEdges();

    /**
     * Finds and returns list of incident edges for <code>vertex</code>
     * Incident - originates from here (In oriented graph edge originates from both ends.
     * So it may be both in outbound or in inbound property of the edge)
     *
     * @param vertex vertex from which to find incident edges
     * @return list of incident edges
     * @throws InvalidVertexException if vertex is invalid or does not belong to the graph
     */
    List<Edge> incidentEdges(Vertex vertex) throws InvalidVertexException;

    /**
     * Returns opposite vertex from edge.
     *
     * @param vertex the first vertex on the edge
     * @param edge   the edge itself
     * @return opposite vertex for current edge
     * @throws InvalidVertexException if vertex is invalid or does not belong to the graph
     * @throws InvalidEdgeException   if edge is invalid or does not belong to the graph
     */
    Vertex opposite(Vertex vertex, Edge edge) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Determines if two vertices are connected with any edge
     *
     * @param vertex1 first vertex
     * @param vertex2 second vertex
     * @return true if connected, false - otherwise
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     */
    boolean areConnected(Vertex vertex1, Vertex vertex2) throws InvalidVertexException;

    /**
     * Inserts new vertex to the graph
     *
     * @param element data for new vertex
     * @return link to newly created vertex
     * @throws InvalidVertexException if there is already a vertex with the same data
     */
    Vertex insertVertex(String element) throws InvalidVertexException;

    /**
     * Inserts new edge to the graph
     *
     * @param vertex1     first vertex link
     * @param vertex2     second vertex link
     * @param edgeElement data to be stored in the edge
     * @return link to newly created edge
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     * @throws InvalidEdgeException   if there is already an edge with the same data and vertices
     */
    Edge insertEdge(Vertex vertex1, Vertex vertex2, int edgeElement) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Inserts new edge to the graph
     *
     * @param vertElement1 first vertex data
     * @param vertElement2 second vertex data
     * @param edgeElement  data to be stored in the edge
     * @return link to newly created edge
     * @throws InvalidVertexException if any of the vertices is invalid or does not belong to the graph
     * @throws InvalidEdgeException   if there is already an edge with the same data and vertices
     */
    Edge insertEdge(String vertElement1, String vertElement2, int edgeElement) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Removes vertex from graph
     *
     * @param vertex link to the vertex to delete
     * @return data from removed vertex
     * @throws InvalidVertexException if vertex is invalid or does not belong to the graph
     */
    String removeVertex(Vertex vertex) throws InvalidVertexException;

    /**
     * Removes edge from graph
     *
     * @param edge link to the edge to delete
     * @return data from removed edge
     * @throws InvalidEdgeException if edge is invalid or does not belong to the graph
     */
    int removeEdge(Edge edge) throws InvalidEdgeException;

    Edge getEdge(Vertex vertexFrom, Vertex vertexTo);

    Edge getEdge(String vertexElFrom, String vertexElTo);

    Vertex getVertex(String vertexEl);
}
