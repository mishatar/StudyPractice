package ru.etu.graphview.drawing;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ru.etu.controllers.LoggerView;
import ru.etu.graph.DirectedGraphList;
import ru.etu.graph.Edge;
import ru.etu.graph.InvalidVertexException;
import ru.etu.graph.Vertex;
import ru.etu.graphview.GraphPane;
import ru.etu.graphview.base.*;
import ru.etu.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This method manage any changes in graph from GUI
 */
public class GraphEditor {

    Logger loggerInstance;

    private GraphPane graphPane; // To have access to pane and graph itself

    private ViewMode viewMode; // To manage current tool

    // For Vertex creation
    private boolean isCreatingVertex = false;
    private FXVertexNode newVertexNode;

    // For Edge creation
    private int currentNode = 0;
    private FXEdge newEdgeNode;
    private boolean movedOppositeEdge = false;
    private Edge connectedEdge;
    private String connectedEdgeNodeWeight;

    // For Edge creation
    private FXVertexNode firstNode;
    private FXVertexNode secondNode;
    private int nodesSelected = 0;

    // For Vertex selection
    private FXVertexNode firstNodeSelect;
    private FXVertexNode secondNodeSelect;
    private int nodesSelected1 = 0;

    private final List<Label> algLabels; // Stores algorithm labels for easy access

    public GraphEditor(GraphPane graphPane) {
        this(graphPane, ViewMode.NORMAL);
    }

    public GraphEditor(GraphPane graphPane, ViewMode viewMode) {
        this.graphPane = graphPane;
        this.viewMode = viewMode;

        algLabels = new ArrayList<>();
        loggerInstance = Logger.getInstance();
    }

    /**
     * Deletes everything from graph (and its view) and resets all instrument data
     */
    public void eraseGraph() {
        clearEditorData();
        graphPane.clearPane();
    }

    /**
     * Resets all instrument data, considering that later graph will be reset as well
     */
    private void clearEditorData() {
        if (currentNode == 2) {
            graphPane.getChildren().remove(newEdgeNode.getLabelField());
            graphPane.getChildren().remove(newEdgeNode.getArrow());
            graphPane.getChildren().remove(newEdgeNode);

            if (graphPane.getGraph().getClass() == DirectedGraphList.class && movedOppositeEdge) {

                var fxEdgeCurve = graphPane.getEdgeMap().get(connectedEdge);

                graphPane.getChildren().remove(fxEdgeCurve.getLabelField());
                graphPane.getChildren().remove(fxEdgeCurve.getArrow());
                graphPane.getChildren().remove(fxEdgeCurve);
            }
        }

        if (isCreatingVertex) {
            graphPane.getChildren().remove(newVertexNode.getLabelField());
            graphPane.getChildren().remove(newVertexNode);
        }


        isCreatingVertex = false;
        newVertexNode = null;
        newEdgeNode = null;
        firstNode = null;
        secondNode = null;
        currentNode = 0;
        movedOppositeEdge = false;
        nodesSelected = 0;
        nodesSelected1 = 0;
    }

    /**
     * Resets all instrument data
     */
    private void resetModes() {
        // Vertex creation mode
        if (isCreatingVertex) {
            graphPane.getChildren().remove(newVertexNode.getLabelField());
            graphPane.getChildren().remove(newVertexNode);
            isCreatingVertex = false;
        }

        // Edge creation mode
        switch (currentNode) {
            case 1 -> firstNode.removeStyleClass("vertex-selected");
            case 2 -> {
                firstNode.removeStyleClass("vertex-selected");
                secondNode.removeStyleClass("vertex-selected");
                graphPane.getChildren().remove(newEdgeNode.getLabelField());
                graphPane.getChildren().remove(newEdgeNode.getArrow());
                graphPane.getChildren().remove(newEdgeNode);

                if (graphPane.getGraph().getClass() == DirectedGraphList.class && movedOppositeEdge) {

                    var fxEdgeCurve = graphPane.getEdgeMap().get(connectedEdge);

                    graphPane.getChildren().remove(fxEdgeCurve.getLabelField());
                    graphPane.getChildren().remove(fxEdgeCurve.getArrow());
                    graphPane.getChildren().remove(fxEdgeCurve.getLabel());
                    graphPane.getChildren().remove(fxEdgeCurve);

                    graphPane.getEdgeMap().remove(connectedEdge);

                    var fxEdgeLine = new FXEdgeLine(connectedEdge, graphPane.getVertexMap().get(connectedEdge.getVertexOutbound()), graphPane.getVertexMap().get(connectedEdge.getVertexInbound()));

                    var label = new Label(connectedEdgeNodeWeight);
                    fxEdgeLine.setLabel(label);
                    graphPane.getChildren().add(label);

                    if (graphPane.getGraphViewProperties().isNeedArrows()) {
                        var arrow = new Arrow(graphPane.getGraphViewProperties().getArrowSize());
                        fxEdgeLine.setArrow(arrow);
                        graphPane.getChildren().add(arrow);
                    }

                    graphPane.getChildren().add(fxEdgeLine);
                    graphPane.getEdgeMap().put(connectedEdge, fxEdgeLine);

                    graphPane.takeVerticesOnFront();
                }
            }
        }

        nodesSelected = 0;

        movedOppositeEdge = false;

        currentNode = 0;

        graphPane.enableVertexDrag();
    }

    /**
     * Creates TextField
     *
     * @return created TextField
     */
    private TextField createTextField() {
        TextField dataInput = new TextField();
        dataInput.setAlignment(Pos.CENTER);
        dataInput.setPrefHeight(10);
        dataInput.setPrefWidth(70);

        return dataInput;
    }

    /**
     * Creates Label view with passed data and removes TextField.
     *
     * @param labelText text to put in label
     * @param node      view object to which to set created label
     * @param textField textField to be deleted
     */
    private void CreateLabelFromTextField(String labelText, LabelledNode node, TextField textField) {
        Label label = new Label(labelText);

        node.setLabel(label);
        graphPane.getChildren().add(label);
        graphPane.getChildren().remove(textField);
    }

    /**
     * Sets instrument by viewMode
     *
     * @param viewMode instrument type to set up
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;

        switch (viewMode) {
            case NORMAL -> {

                resetModes();

                graphPane.setOnMousePressed((e) -> {
                });
                graphPane.setOnMouseEntered(event -> {
                });
                graphPane.setOnMouseExited(event -> {
                });
                graphPane.setOnKeyPressed((e) -> {
                });

            }
            case VERTEX_PLACEMENT -> {

                resetModes();

                graphPane.setOnMousePressed((e) -> {
                    if (e.isPrimaryButtonDown() && !isCreatingVertex) {
                        isCreatingVertex = true;

                        newVertexNode = new FXVertexNode(
                                e.getX(),
                                e.getY(),
                                graphPane.getGraphViewProperties().getVerticesRadius());

                        TextField vertexDataInput = createTextField();

                        newVertexNode.setLabelField(vertexDataInput);

                        graphPane.getChildren().add(vertexDataInput);
                        graphPane.getChildren().add(newVertexNode);
                    }
                });

                graphPane.setOnKeyPressed((e) -> {
                    if (e.getCode() == KeyCode.ENTER && isCreatingVertex) {
                        String data = newVertexNode.getLabelField().getText();

                        if (data.length() == 0) {
                            loggerInstance.printMessage(getClass().getName(), "Vertex name can't be empty.", true);
                        } else if (data.contains(" ")) {
                            loggerInstance.printMessage(getClass().getName(), "Vertex name can't have spaces in its name.", true);
                        } else {
                            Vertex newVertex;
                            try {
                                newVertex = graphPane.getGraph().insertVertex(data);

                                newVertexNode.setVertex(newVertex);
                                graphPane.getVertexMap().put(newVertex, newVertexNode);

                                CreateLabelFromTextField(newVertexNode.getVertex().getData(), newVertexNode, newVertexNode.getLabelField());

                                isCreatingVertex = false;

                            } catch (InvalidVertexException exception) {
                                loggerInstance.printMessage(getClass().getName(), "Vertex with the same name already exists! Try another name.", true);
                            }
                        }
                    } else if (e.getCode() == KeyCode.ESCAPE && isCreatingVertex) {
                        graphPane.getChildren().remove(newVertexNode.getLabelField());
                        graphPane.getChildren().remove(newVertexNode);
                        isCreatingVertex = false;
                    }
                });

                graphPane.setOnMouseEntered(event -> {
                    if (!event.isPrimaryButtonDown()) {
                        graphPane.getScene().setCursor(Cursor.HAND);
                    }
                });

                graphPane.setOnMouseExited(event -> {
                    if (!event.isPrimaryButtonDown()) {
                        graphPane.getScene().setCursor(Cursor.DEFAULT);
                    }
                });

            }
            case EDGE_PLACEMENT -> {
                resetModes();

                graphPane.disableVertexDrag();

                graphPane.setOnMousePressed((e) -> {

                    if (e.isPrimaryButtonDown() && currentNode == 0) {
                        if (e.getPickResult().getIntersectedNode().getClass() == FXVertexNode.class) {
                            firstNode = (FXVertexNode) e.getPickResult().getIntersectedNode();

                            firstNode.addStyleClass("vertex-selected");

                            currentNode = 1;
                        } else {
                            loggerInstance.printMessage(getClass().getName(), "Not a vertex! Pointed object is: " + e.getPickResult().getIntersectedNode().getClass());
                        }
                    } else if (e.isPrimaryButtonDown() && currentNode == 1) {
                        if (e.getPickResult().getIntersectedNode().getClass() == FXVertexNode.class) {
                            secondNode = (FXVertexNode) e.getPickResult().getIntersectedNode();

                            if (firstNode == secondNode) {
                                loggerInstance.printMessage(getClass().getName(), "You chose the same vertices! Try another again.", true);
                            } else if (graphPane.getGraph().areConnected(firstNode.getVertex(), secondNode.getVertex())) {
                                loggerInstance.printMessage(getClass().getName(), "Those Vertices are already connected! Try another pair.", true);
                            } else {
                                secondNode.addStyleClass("vertex-selected");
                                //if directed, else - undirected
                                if (graphPane.getGraph().getClass() == DirectedGraphList.class && graphPane.getGraph().areConnected(secondNode.getVertex(), firstNode.getVertex())) {

                                    movedOppositeEdge = true;

                                    connectedEdge = graphPane.getGraph().getEdge(secondNode.getVertex(), firstNode.getVertex());
                                    connectedEdgeNodeWeight = graphPane.getEdgeMap().get(connectedEdge).getLabel().getText();

                                    graphPane.removeEdgeNode(graphPane.getEdgeMap().get(connectedEdge));
                                    graphPane.getEdgeMap().remove(connectedEdge);

                                    var fxConnectedEdge = new FXEdgeCurve(connectedEdge, secondNode, firstNode, -1, graphPane.getGraphViewProperties().getCurveEdgeAngle());
                                    var fxConnectedEdgeLabel = new Label(connectedEdgeNodeWeight);
                                    fxConnectedEdge.setLabel(fxConnectedEdgeLabel);


                                    if (graphPane.getGraphViewProperties().isNeedArrows()) {
                                        var arrow = new Arrow(graphPane.getGraphViewProperties().getArrowSize());
                                        fxConnectedEdge.setArrow(arrow);
                                    }

                                    newEdgeNode = new FXEdgeCurve(firstNode, secondNode, 1, graphPane.getGraphViewProperties().getCurveEdgeAngle());

                                    if (graphPane.getGraphViewProperties().isNeedArrows()) {
                                        var arrow = new Arrow(graphPane.getGraphViewProperties().getArrowSize());
                                        newEdgeNode.setArrow(arrow);
                                        graphPane.getChildren().add(arrow);
                                    }

                                    newEdgeNode.addStyleClass("edge-in-creation");

                                    graphPane.addEdgeNode(fxConnectedEdge, connectedEdge);

                                    graphPane.getChildren().add((FXEdgeCurve) newEdgeNode);


                                } else {
                                    newEdgeNode = new FXEdgeLine(firstNode, secondNode);

                                    if (graphPane.getGraphViewProperties().isNeedArrows()) {
                                        var arrow = new Arrow(graphPane.getGraphViewProperties().getArrowSize());
                                        newEdgeNode.setArrow(arrow);
                                        graphPane.getChildren().add(arrow);
                                    }

                                    newEdgeNode.addStyleClass("edge-in-creation");
                                    graphPane.getChildren().add((FXEdgeLine) newEdgeNode);
                                }

                                graphPane.takeVerticesOnFront();

                                TextField edgeDataInput = createTextField();
                                newEdgeNode.setLabelField(edgeDataInput);
                                graphPane.getChildren().add(edgeDataInput);

                                currentNode = 2;
                            }

                        } else {
                            loggerInstance.printMessage(getClass().getName(), "Not a vertex! Pointed object is: " + e.getPickResult().getIntersectedNode().getClass());
                        }
                    }
                });

                graphPane.setOnKeyPressed((e) -> {
                    if (e.getCode() == KeyCode.ENTER && currentNode == 2) {

                        int data;
                        Edge newEdge;

                        try {
                            data = Integer.parseInt(newEdgeNode.getLabelField().getText());

                            if (data <= 0) {
                                loggerInstance.printMessage(getClass().getName(), "Entered value is less then 0 or equal it! Try again.", true);
                            } else {
                                newEdge = graphPane.getGraph().insertEdge(firstNode.getVertex(), secondNode.getVertex(), data);

                                newEdgeNode.setEdge(newEdge);
                                graphPane.getEdgeMap().put(newEdge, newEdgeNode);

                                CreateLabelFromTextField(Integer.toString(data), newEdgeNode, newEdgeNode.getLabelField());

                                newEdgeNode.removeStyleClass("edge-in-creation");
                                firstNode.removeStyleClass("vertex-selected");
                                secondNode.removeStyleClass("vertex-selected");

                                currentNode = 0;
                            }
                        } catch (NumberFormatException numberFormatException) {
                            loggerInstance.printMessage(getClass().getName(), "Entered value is not a number! Try again.", true);
                        }

                    } else if (e.getCode() == KeyCode.ESCAPE && currentNode == 2) {
                        firstNode.removeStyleClass("vertex-selected");
                        secondNode.removeStyleClass("vertex-selected");
                        graphPane.getChildren().remove(newEdgeNode.getLabelField());
                        graphPane.getChildren().remove(newEdgeNode.getArrow());
                        graphPane.getChildren().remove(newEdgeNode);
                        currentNode = 0;

                        if (graphPane.getGraph().getClass() == DirectedGraphList.class && movedOppositeEdge) {

                            var fxEdgeCurve = graphPane.getEdgeMap().get(connectedEdge);

                            graphPane.getChildren().remove(fxEdgeCurve.getLabelField());
                            graphPane.getChildren().remove(fxEdgeCurve.getArrow());
                            graphPane.getChildren().remove(fxEdgeCurve.getLabel());
                            graphPane.getChildren().remove(fxEdgeCurve);

                            graphPane.getEdgeMap().remove(connectedEdge);

                            var fxEdgeLine = new FXEdgeLine(connectedEdge, graphPane.getVertexMap().get(connectedEdge.getVertexOutbound()), graphPane.getVertexMap().get(connectedEdge.getVertexInbound()));

                            var label = new Label(connectedEdgeNodeWeight);
                            fxEdgeLine.setLabel(label);
                            graphPane.getChildren().add(label);

                            if (graphPane.getGraphViewProperties().isNeedArrows()) {
                                var arrow = new Arrow(graphPane.getGraphViewProperties().getArrowSize());
                                fxEdgeLine.setArrow(arrow);
                                graphPane.getChildren().add(arrow);
                            }

                            graphPane.getChildren().add(fxEdgeLine);
                            graphPane.getEdgeMap().put(connectedEdge, fxEdgeLine);

                            graphPane.takeVerticesOnFront();
                        }

                        movedOppositeEdge = false;

                    }
                });
            }
            case VERTEX_CHOOSE -> {
                resetModes();

                graphPane.disableVertexDrag();

                graphPane.setOnMousePressed((e) -> {

                    if (e.isPrimaryButtonDown() && nodesSelected1 == 0) {
                        if (e.getPickResult().getIntersectedNode().getClass() == FXVertexNode.class) {
                            firstNodeSelect = (FXVertexNode) e.getPickResult().getIntersectedNode();

                            firstNodeSelect.addStyleClass("vertex-selected-for-algorithm-1");

                            nodesSelected1 = 1;
                        } else {
                            loggerInstance.printMessage(getClass().getName(), "Not a vertex! Pointed object is: " + e.getPickResult().getIntersectedNode().getClass());
                        }
                    } else if (e.isPrimaryButtonDown() && nodesSelected1 == 1) {
                        if (e.getPickResult().getIntersectedNode().getClass() == FXVertexNode.class) {
                            secondNodeSelect = (FXVertexNode) e.getPickResult().getIntersectedNode();

                            if (firstNodeSelect == secondNodeSelect) {
                                firstNodeSelect.removeStyleClass("vertex-selected-for-algorithm-1");
                                firstNodeSelect = null;
                                nodesSelected1 = 0;
                            } else {
                                secondNodeSelect.addStyleClass("vertex-selected-for-algorithm-2");

                                nodesSelected1 = 2;
                            }
                        } else {
                            loggerInstance.printMessage(getClass().getName(), "Not a vertex! Pointed object is: " + e.getPickResult().getIntersectedNode().getClass());
                        }
                    } else if (e.isPrimaryButtonDown() && nodesSelected1 == 2) {
                        if (e.getPickResult().getIntersectedNode().getClass() == FXVertexNode.class) {
                            var node = (FXVertexNode) e.getPickResult().getIntersectedNode();

                            if (node == firstNodeSelect) {
                                firstNodeSelect.removeStyleClass("vertex-selected-for-algorithm-1");
                                secondNodeSelect.removeStyleClass("vertex-selected-for-algorithm-2");
                                firstNodeSelect = secondNodeSelect;
                                firstNodeSelect.addStyleClass("vertex-selected-for-algorithm-1");
                                secondNodeSelect = null;
                                nodesSelected1 = 1;
                            } else if (node == secondNodeSelect) {
                                secondNodeSelect.removeStyleClass("vertex-selected-for-algorithm-2");
                                secondNodeSelect = null;
                                nodesSelected1 = 1;
                            }
                        } else {
                            loggerInstance.printMessage(getClass().getName(), "Not a vertex! Pointed object is: " + e.getPickResult().getIntersectedNode().getClass());
                        }
                    }
                });

            }
        }
    }

    /**
     * Adds label for algorithm data to be stored in.
     *
     * @param startVertex vertex link to which to pin new label
     */
    public void addAlgLabels(Vertex startVertex) {

        for (var fxVertex : graphPane.getVertexMap().values()) {

            Label algLabel;

            if (fxVertex.getVertex().equals(startVertex)) {
                algLabel = new Label("0");
            } else {
                algLabel = new Label("inf");
            }

            fxVertex.setAlgLabel(algLabel);

            graphPane.getChildren().add(algLabel);

            algLabels.add(algLabel);
        }
    }

    /**
     * Removes all algorithm labels
     */
    public void removeAlgLabels() {

        if (algLabels.size() == 0)
            throw new RuntimeException("You need to create labels before removing them.");

        for (var label : algLabels) {
            graphPane.getChildren().remove(label);
        }

        algLabels.clear();
    }

    /**
     * Restores initial data in all algorithm labels
     */
    public void clearAlgLabels() {
        for (var fxVertex : graphPane.getVertexMap().values()) {

            Label algLabel = fxVertex.getAlgLabel();

            if (!algLabel.getText().equals("0")) {
                algLabel.setText("inf");
            }
        }
    }


    public ViewMode getViewMode() {
        return viewMode;
    }

    public GraphPane getGraphPane() {
        return graphPane;
    }

    public boolean isCreatingVertex() {
        return isCreatingVertex;
    }

    public FXVertexNode getNewVertexNode() {
        return newVertexNode;
    }

    public int getCurrentNode() {
        return currentNode;
    }

    public FXEdge getNewEdgeNode() {
        return newEdgeNode;
    }

    public FXVertexNode getFirstNode() {
        return firstNode;
    }

    public FXVertexNode getSecondNode() {
        return secondNode;
    }

    public int getNodesSelected() {
        return nodesSelected;
    }

    public FXVertexNode getFirstNodeSelect() {
        return firstNodeSelect;
    }

    public FXVertexNode getSecondNodeSelect() {
        return secondNodeSelect;
    }

    public int getNodesSelected1() {
        return nodesSelected1;
    }
}
