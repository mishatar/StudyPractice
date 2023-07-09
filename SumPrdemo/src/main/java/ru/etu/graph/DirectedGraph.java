package ru.etu.graph;

import java.util.List;

/**
 * Ориентированный граф — это набор вершин, соединенных ребрами. Ребра имеют направление.
 */
public interface DirectedGraph extends Graph {

    /**
     * Находит и возвращает список инцидентных ребер, которые начинаются с vertexFrom
      *
      * @param vertexFrom вершина, из которой нужно найти инцидентные ребра, направленные из нее.
      * @return список инцидентных ребер, направленных из vertexFrom
     */
    @Override
    List<Edge> incidentEdges(Vertex vertexFrom);

    /**
     * Находит и возвращает список инцидентных ребер, которые заканчиваются в vertexTo
     *
     * @param vertexTo вершина, из которой нужно найти инцидентные ребра, заканчивающиеся в ней
     * @return список инцидентных ребер, которые заканчиваются в vertexTo
     */
    List<Edge> inboundEdges(Vertex vertexTo);

    /**
     * Определяет, соединены ли две вершины каким-либо ребром
     *
     * @param vertexOut начальная вершина
     * @param vertexIn  конечная вершина
     * @return true если соеденены
     */
    @Override
    boolean areConnected(Vertex vertexOut, Vertex vertexIn);

    /**
     * Вставляет новое ребро в граф
     *
     * @param vertexOut   начальная вершина 
     * @param vertexIn    конечная вершина
     * @param edgeElement данные о добавленной вершине
     * @return только что созданное ребро
     */
    @Override
    Edge insertEdge(Vertex vertexOut, Vertex vertexIn, int edgeElement);

    /**
     * Вставляет новое ребро в граф
     *
     * @param vertexOut   начальная вершина 
     * @param vertexIn    конечная вершина
     * @param edgeElement данные о добавленной вершине
     * @return только что созданное ребро
     */
    @Override
    Edge insertEdge(String vertElement1, String vertElement2, int edgeElement);
}
