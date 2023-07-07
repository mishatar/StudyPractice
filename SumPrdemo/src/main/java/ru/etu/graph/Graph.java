package ru.etu.graph;

import java.util.List;

/**
 * Граф представляет собой набор вершин, соединенных ребрами. Ребра не имеют направления.
 *
 * @see Edge
 * @see Vertex
 */
public interface Graph {

    /* Метод, возвращающий количество вершин в графе */
    int verticesNum();

    /*  Метод, возвращающий количество ребер в графе*/
    int edgesNum();

    /*Метод, возвращающий список вершин*/
    List<Vertex> getVertices();

    /* Метод, возвращающий список ребер*/
    List<Edge> getEdges();

    /**
     * Находит и возвращает список инцидентных ребер для <code>vertex</code>
     * В ориентированном графе ребро исходит из обоих концов.
     * Incident - originates from here (In oriented graph edge originates from both ends.
     * So it may be both in outbound or in inbound property of the edge)
     *
     * @param vertex вершина, из которой нужно найти инцидентные ребра
      * @return список инцидентных ребер
     */
    List<Edge> incidentEdges(Vertex vertex);

    /**
     * Возвращает противоположную вершину.
     *
     * @param vertex первая вершина
     * @param edge   само ребро
     * @return противоположная вершина
     */
    Vertex opposite(Vertex vertex, Edge edge);

    /**
     * Определяет, соединены ли две вершины каким-либо ребром
      *
      * @param vertex1 первая вершина
      * @param vertex2 вторая вершина
      * @return true если подключено, false - иначе
     */
    boolean areConnected(Vertex vertex1, Vertex vertex2);

    /**
     * Добавляет новую вершину в граф
     *
     * @param element данные для новой вершинв
     * @return возвращает только созданную вершину
     */
    Vertex insertVertex(String element);

    /**
     * Добавляет новое ребро в граф
     *
     * @param vertex1     первая вешина
     * @param vertex2     вторая вершина
     * @param edgeElement данные о ребре
     * @return возвращает только созданное ребро
     */
    Edge insertEdge(Vertex vertex1, Vertex vertex2, int edgeElement);

    /**
     * Inserts new edge to the graph
     *
     * @param vertElement1 first vertex data
     * @param vertElement2 second vertex data
     * @param edgeElement  data to be stored in the edge
     * @return link to newly created edge
     */
    Edge insertEdge(String vertElement1, String vertElement2, int edgeElement);

    /**
     * Удаляет вершину из графа
     *
     * @param vertex вершина которую нужно удалить
     * @return данные удаленной вершины
     */
    String removeVertex(Vertex vertex);

    /**
     * Удаляет ребро из графа
     *
     * @param edge ребро которое надо удалить
     * @return данные удаленного ребра
     */
    int removeEdge(Edge edge);

    Edge getEdge(Vertex vertexFrom, Vertex vertexTo);

    Edge getEdge(String vertexElFrom, String vertexElTo);

    Vertex getVertex(String vertexEl);
}
