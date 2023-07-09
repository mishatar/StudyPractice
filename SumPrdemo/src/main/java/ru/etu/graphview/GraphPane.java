package ru.etu.graphview;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import ru.etu.graph.Edge;
import ru.etu.graph.Graph;
import ru.etu.graph.Vertex;
import ru.etu.graphview.base.*;
import ru.etu.graphview.styling.StyleEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main place for all graph view elements to base on.
 * It is responsible for managing storage and view/interaction properties
 */
public class GraphPane extends Pane {

    // Stored view properties
    private GraphViewProperties graphViewProperties;

    private Graph graph;
    private Map<Vertex, FXVertexNode> vertexMap;
    private Map<Edge, FXEdge> edgeMap;

    private StyleEngine styleEngine;

    // Controls if graph was set
    private boolean graphSet = false;

    public GraphPane() {
        edgeMap = new HashMap<>();
        vertexMap = new HashMap<>();

        getStylesheets().add("graphStyles.css");
        getStyleClass().add("graph-pane");
    }

    /**
     * Inserts graph to GraphPane. Creates all necessary view components.
     *
     * @param graph      graph link
     * @param properties view properties
     */
    public void loadGraph(Graph graph, GraphViewProperties properties) {
        clearPane();

        this.graph = graph;
        graphViewProperties = properties;

        for (var vertex : graph.getVertices()) {
            FXVertexNode fxVertex = new FXVertexNode(vertex, 0, 0,
                    properties.getVerticesRadius());
            vertexMap.put(vertex, fxVertex);
        }

        for (var edge : graph.getEdges()) {
            var fxVertexFrom = vertexMap.get(edge.getVertexOutbound());
            var fxVertexTo = vertexMap.get(edge.getVertexInbound());

            if (properties.isNeedDoubleEdges()) {
                var backwardsEdge = graph.getEdge(fxVertexTo.getVertex(), fxVertexFrom.getVertex());
                if (backwardsEdge != null) {
                    if (!edgeMap.containsKey(backwardsEdge) && !edgeMap.containsKey(edge)) {
                        var fxBackwardsEdge = new FXEdgeCurve(backwardsEdge, fxVertexTo, fxVertexFrom, -1, graphViewProperties.getCurveEdgeAngle());
                        var fxEdge = new FXEdgeCurve(edge, fxVertexFrom, fxVertexTo, 1, graphViewProperties.getCurveEdgeAngle());

                        addEdgeNode(fxBackwardsEdge, backwardsEdge);
                        addEdgeNode(fxEdge, edge);
                    }
                } else {
                    var fxEdge = new FXEdgeLine(edge, fxVertexFrom, fxVertexTo);

                    addEdgeNode(fxEdge, edge);
                }
            } else {
                var fxEdge = new FXEdgeLine(edge, fxVertexFrom, fxVertexTo);

                addEdgeNode(fxEdge, edge);
            }
        }

        for (var vertexNode : vertexMap.values()) {
            addVertexNode(vertexNode);
        }

        RandomStrategy.place(
                this.widthProperty().doubleValue(),
                this.heightProperty().doubleValue(),
                vertexMap.values());

        graphSet = true;
    }

    /**
     * Clears GraphPane by deleting all view objects and all vertices from the graph
     * (edges delete automatically)
     */
    public void clearPane() {
        for (var edgeNode : edgeMap.values()) {
            removeEdgeNode(edgeNode);
        }

        for (var vertexNode : vertexMap.values()) {
            removeVertexNode(vertexNode);
        }

        for (var vertex : vertexMap.keySet()) {
            graph.removeVertex(vertex);
        }

        edgeMap.clear();
        vertexMap.clear();
    }

    /**
     * Adds vertex view and it's label to the screen
     *
     * @param vertexNode vertex view
     */
    private void addVertexNode(FXVertexNode vertexNode) {
        this.getChildren().add(vertexNode);

        Label label = new Label(vertexNode.getVertex().getData());
        vertexNode.setLabel(label);
        this.getChildren().add(label);
    }

    /**
     * Adds edge view and it's label (and arrow if needed) to the screen
     *
     * @param fxEdge edge view
     * @param edge   edge link
     */
    public void addEdgeNode(FXEdge fxEdge, Edge edge) {
        if (fxEdge.getClass() == FXEdgeLine.class) {
            this.getChildren().add((FXEdgeLine) fxEdge);
        } else if (fxEdge.getClass() == FXEdgeCurve.class) {
            this.getChildren().add((FXEdgeCurve) fxEdge);
        } else {
            throw new RuntimeException("Can't determine edge class: " + fxEdge.getClass());
        }

        edgeMap.put(edge, fxEdge);

        Label label = new Label(Integer.toString(fxEdge.getEdge().getData()));
        fxEdge.setLabel(label);
        this.getChildren().add(label);

        if (graphViewProperties.isNeedArrows()) {
            Arrow arrow = new Arrow(graphViewProperties.getArrowSize());
            fxEdge.setArrow(arrow);
            this.getChildren().add(arrow);
        }
    }

    /**
     * Removes vertex view from the screen with all its additional objects
     *
     * @param vertexNode vertex view
     */
    public void removeVertexNode(FXVertexNode vertexNode) {
        this.getChildren().remove(vertexNode);
        this.getChildren().remove(vertexNode.getLabel());

        if (vertexNode.getAlgLabel() != null) {
            this.getChildren().remove(vertexNode.getAlgLabel());
        }
    }

    /**
     * Removes edge view from the screen with all its additional objects
     *
     * @param fxEdge edge view
     */
    public void removeEdgeNode(FXEdge fxEdge) {
        this.getChildren().remove(fxEdge);
        this.getChildren().remove(fxEdge.getLabel());
        if (fxEdge.getArrow() != null) {
            this.getChildren().remove(fxEdge.getArrow());
        }
    }

    /**
     * Enables dragging mechanism in vertices
     */
    public void enableVertexDrag() {
        for (var fxVertex : vertexMap.values()) {
            fxVertex.enableDrag();
        }
    }

    /**
     * Disables dragging mechanism in vertices
     */
    public void disableVertexDrag() {
        for (var fxVertex : vertexMap.values()) {
            fxVertex.disableDrag();
        }
    }

    /**
     * Replaces vertices, so they were on top of any other object in GraphPane
     */
    public void takeVerticesOnFront() {
        // Replacing vertices so that they were on front
        List<Node> vertexNodes = new ArrayList<>();

        for (var node : this.getChildren()) {
            if (node.getClass() == FXVertexNode.class) {
                vertexNodes.add(node);
            }
        }

        for (var vertexNode : vertexNodes) {
            this.getChildren().remove(vertexNode);
            this.getChildren().add(vertexNode);
        }
    }

    public GraphViewProperties getGraphViewProperties() {
        return graphViewProperties;
    }

    public Graph getGraph() {
        return graph;
    }

    public Map<Vertex, FXVertexNode> getVertexMap() {
        return vertexMap;
    }

    public Map<Edge, FXEdge> getEdgeMap() {
        return edgeMap;
    }

    public boolean isGraphSet() {
        return graphSet;
    }
}
