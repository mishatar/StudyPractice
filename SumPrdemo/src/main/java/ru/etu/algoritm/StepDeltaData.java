package ru.etu.algoritm;

public class StepDeltaData {
    private StepType type;
    private VertexDijkstra current;
    private VertexDijkstra child;
    private int weight;

    public StepDeltaData(StepType type, VertexDijkstra current, VertexDijkstra child, int weight) {
        this.type = type;
        this.current = current;
        this.child = child;
        this.weight = weight;
    }

    public StepType getType() {
        return type;
    }

    public VertexDijkstra getCurrent() {
        return current;
    }

    public VertexDijkstra getChild() {
        return child;
    }

    public int getWeight() {
        return weight;
    }
}
