package ru.etu.algoritm;

import ru.etu.graph.Vertex;

import java.util.Objects;

/**
 * Расширенная версия Vertex для аогоритма дейкстры с данными о родителе
 */
public class VertexDijkstra extends Vertex {

    private VertexDijkstra parent;

    private final Vertex original;

    /**
     * конструктор
     *
     * @param vertex оригинальная вершина
     */
    public VertexDijkstra(Vertex vertex) {
        super(vertex.getData());

        original = vertex;
    }

    /**
     * устанавливает вершину родителя (вершина с кратчайшим путем от начала)
     *
     * @param parent вершина родителя
     */
    public void setParent(VertexDijkstra parent) {
        this.parent = parent;
    }

    /**
     * Получение вершины родителя (вершина с кратчайшим путем от начала)
     *
     * @return вершина родителя
     */
    public VertexDijkstra getParent() {
        return parent;
    }

    /**
     * возвращает оригинальную вершину
     */
    public Vertex getOriginal() {
        return original;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Vertex.class) return o.equals(this);
        if (!(o instanceof VertexDijkstra)) return false;
        return Objects.equals(((VertexDijkstra) o).getData(), this.getData());
    }
}
