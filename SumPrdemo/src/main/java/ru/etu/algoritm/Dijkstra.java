package ru.etu.algoritm;

import javafx.util.Pair;
import ru.etu.graph.Edge;
import ru.etu.graph.Graph;
import ru.etu.graph.Vertex;

import java.util.*;

public class Dijkstra {

    /**
     * stack with shortest path from beginning to end
     */
    private Stack<Vertex> path;
    /**
     * Graph to use for pathfinding
     */
    private Graph graph;
    private ArrayList<StepDeltaData> steps;

    public Dijkstra() {
    }

    /**
     * Constructor
     *
     * @param graph graph to use for pathfinding
     */
    public Dijkstra(Graph graph) {
        this.graph = graph;
        path = new Stack<>();
    }

    /**
     * Method to launch pathfinding. Founded path can be accessed via getPath() method.
     *
     * @param currentInput pathfinding beginning vertex
     * @param toInput      pathfinding end vertex
     * @return true on success (path found), false on failure (path not found)
     */
    public boolean findPath(Vertex currentInput, Vertex toInput) {
        path.removeAllElements();
        steps = new ArrayList<>();
        VertexDijkstra current = new VertexDijkstra(currentInput);
        VertexDijkstra to = new VertexDijkstra(toInput);

        PriorityQueue<Pair<Integer, VertexDijkstra>> queue = new PriorityQueue<>(Comparator.comparingInt(Pair::getKey));
        HashMap<String, Pair<Integer, VertexDijkstra>> nodesToProcess = new HashMap<>();
        ArrayList<String> checkedVertexes = new ArrayList<>();

        int addPoints = 0;

        while (!current.equals(to)) {
            ArrayList<VertexDijkstra> vertexes = getConnectedVertexes(current);
            for (Vertex elem : vertexes) {
                if (checkedVertexes.contains(elem.getData())) {
                    continue;
                }
                VertexDijkstra output = new VertexDijkstra(elem);
                int vertexCost = addPoints + getNodeEdgeWeight(current, output);
                /* если мы не проходили эту вершину, то else*/
                if (nodesToProcess.containsKey(output.getData())) {
                    if (nodesToProcess.get(output.getData()).getKey() > vertexCost) {
                        createStep(StepType.UPDATE, current, output, vertexCost);
                        queue.add(new Pair<>(vertexCost, output));
                        nodesToProcess.put(output.getData(), new Pair<>(vertexCost, output));
                        output.setParent(current);
                    }
                } else {
                    createStep(StepType.UPDATE, current, output, vertexCost);
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
            createStep(StepType.MOVE, current.getParent(), current, addPoints);
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
     * Method of finding a distance betveen 2 Vertexes
     *
     * @param from parent vertex
     * @param to   current vertex
     * @return distance between vertexes
     */
    private int getNodeEdgeWeight(Vertex from, Vertex to) {
        var edge = graph.getEdge(from, to);

        if (edge != null) {
            return edge.getData();
        }

        return Integer.MAX_VALUE;
    }

    private ArrayList<VertexDijkstra> getConnectedVertexes(VertexDijkstra current) {
        ArrayList<VertexDijkstra> vertexes = new ArrayList<>();
        List<Edge> edges = graph.incidentEdges(current);
        for (Edge elem : edges) {
            vertexes.add(new VertexDijkstra(graph.opposite(current, elem)));
        }
        return vertexes;
    }

    /**
     * Get a path from last find iteration
     *
     * @return founded path
     */
    public List<String> getPath() {
        return path.stream().map(Vertex::getData).toList();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    private void createStep(StepType type, VertexDijkstra current, VertexDijkstra child, int weight) {
        steps.add(new StepDeltaData(type, current, child, weight));
    }

    public ArrayList<StepDeltaData> getSteps() {
        return steps;
    }
}
