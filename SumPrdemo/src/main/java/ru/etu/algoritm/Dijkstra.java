package ru.etu.algoritm;

import javafx.util.Pair;
import ru.etu.graph.Edge;
import ru.etu.graph.Graph;
import ru.etu.graph.Vertex;

import java.util.*;

public class Dijkstra {

    /**
     * стек с кратчайшим путем от начала до конца
     */
    private Stack<Vertex> path;
    /**
     * Граф для поиска пути
     */
    private Graph graph;

    public Dijkstra() {
    }

    /**
     * Конструктор
     *
     *  Граф для поиска пути
     */
    public Dijkstra(Graph graph) {
        this.graph = graph;
        path = new Stack<>();
    }

    /**
     * Метод для запуска поиска пути. Доступ к найденному пути можно получить с помощью метода getPath().
     *
     * @param currentInput начальная вершина поиска пути
     * @param toInput конечная вершина поиска пути
     * @return true в случае успеха (путь найден), false в случае неудачи (путь не найден)
     */
    public boolean findPath(Vertex currentInput, Vertex toInput) {
        path.removeAllElements();
        VertexDijkstra current = new VertexDijkstra(currentInput);
        VertexDijkstra to = new VertexDijkstra(toInput);

        PriorityQueue<Pair<Integer, VertexDijkstra>> queue = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
        HashMap<String, Pair<Integer, VertexDijkstra>> nodesToProcess = new HashMap<>();
        ArrayList<String> checkedVertexes = new ArrayList<>();

        int addPoints = 0;

        while (!current.equals(to)) {
            /*создаем массив из смежных верштн */
            ArrayList<VertexDijkstra> vertexes = getConnectedVertexes(current);
            for (Vertex elem : vertexes) {
                /*если мы проходили эту вершину, то идем дальше */
                if (checkedVertexes.contains(elem.getData())) {
                    continue;
                }
                VertexDijkstra output = new VertexDijkstra(elem);
                /*считаем стоимость пути до вершины */
                int vertexCost = addPoints + getNodeEdgeWeight(current, output);
                /* если мы не проходили эту вершину, то else*/
                if (nodesToProcess.containsKey(output.getData())) {
                    /* если путь до этой вершины меньше предыдущей, то обновляем данные*/
                    if (nodesToProcess.get(output.getData()).getKey() > vertexCost) {
                        queue.add(new Pair<>(vertexCost, output));
                        nodesToProcess.put(output.getData(), new Pair<>(vertexCost, output));
                        output.setParent(current);
                    }
                } else {
                    nodesToProcess.put(output.getData(), new Pair<>(vertexCost, output));
                    queue.add(new Pair<>(vertexCost, output));
                    output.setParent(current);
                }
            }

            if (queue.isEmpty()) {
                return false;
            }
            checkedVertexes.add(current.getData());
            current = queue.remove().getValue();
            addPoints = nodesToProcess.get(current.getData()).getKey();
        }
        Stack<Vertex> reverse = new Stack<>();
        while (current.getParent() != null) {
            reverse.add(current);
            current = current.getParent();
        }
        reverse.add(current);
        while (!reverse.isEmpty()) {
            path.add(reverse.pop());
        }
        return true;
    }

    /**
     * Метод нахождения расстояния между двумя вершинами
     * @param from вершина родителя 
     * @param to текущая вершина
     * @return расстояние между вершинами
     */
    private int getNodeEdgeWeight(Vertex from, Vertex to) {
        var edge = graph.getEdge(from, to);

        if (edge != null) {
            return edge.getData();
        }

        return Integer.MAX_VALUE;
    }

    /* возвращает список всех смежных вершин вершин*/
    private ArrayList<VertexDijkstra> getConnectedVertexes(VertexDijkstra current) {
        ArrayList<VertexDijkstra> vertexes = new ArrayList<>();
        List<Edge> edges = graph.incidentEdges(current);
        for (Edge elem : edges) {
            vertexes.add(new VertexDijkstra(graph.opposite(current, elem)));
        }
        return vertexes;
    }


    public List<String> getPath() {
        return path.stream().map(Vertex::getData).toList();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
