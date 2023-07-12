package ru.etu.graphview.drawing;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import ru.etu.algoritm.Dijkstra;
import ru.etu.algoritm.StepType;
import ru.etu.graph.Edge;
import ru.etu.graph.Vertex;
import ru.etu.graphview.GraphPane;
import ru.etu.graphview.base.Label;
import ru.etu.graphview.drawing.multithreading.MyRunnable;
import ru.etu.graphview.drawing.multithreading.ResourceLock;
import ru.etu.graphview.drawing.multithreading.ThreadA;
import ru.etu.graphview.drawing.multithreading.ThreadB;
import ru.etu.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for interpreting data from Dijkstra steps
 * and visualising it on the graph view.
 *
 * @see Dijkstra
 */
public class DijkstraStepEngine {

    Logger loggerInstance;

    /**
     * Delay before each step
     */
    private int stepsDelay;

    private GraphEditor graphEditor;
    private GraphPane graphPane;

    private GraphPainter graphPainter;

    // private AppConsole appConsole;

    private Dijkstra dijkstra;

    /**
     * Threads and their data for multithreading
     */
    private ResourceLock resourceLock;
    private ThreadA threadA;
    private ThreadB threadB;

    private boolean initialised;
    private boolean isPaused;
    private boolean pathExists;

    /**
     * Data for completed steps. Needed for stepping backwards and forward
     */
    private List<StepType> lastTypeList;
    private List<Edge> lastEdgeList;
    private List<Vertex> lastVertexList;
    private List<Label> lastLabelList;
    private List<String> lastWeightList;
    private List<List<String>> lastPathList;
    private List<String> lastMsgList;

    public DijkstraStepEngine(GraphEditor graphEditor, int delayMs) {
        this.graphEditor = graphEditor;
        graphPane = graphEditor.getGraphPane();
        dijkstra = new Dijkstra(graphPane.getGraph());
        graphPainter = GraphPainter.getInstance(graphPane);

        initialised = false;
        isPaused = true;

        lastTypeList = new ArrayList<>();
        lastEdgeList = new ArrayList<>();
        lastVertexList = new ArrayList<>();
        lastLabelList = new ArrayList<>();
        lastWeightList = new ArrayList<>();
        lastPathList = new ArrayList<>();
        lastMsgList = new ArrayList<>();

        stepsDelay = delayMs;
    }

    /**
     * Initialisation method for the class. It performs Dijkstra search, then takes steps and forms
     * a list of actions needed to animate each step.
     */
    public void applyDijkstra() {
        if (graphEditor.getNodesSelected1() != 2)
            throw new IllegalStateException("Cannot run Dijkstra algorithm without 2 nodes!");

        loggerInstance = Logger.getInstance();

        pathExists = dijkstra.findPath(graphEditor.getFirstNodeSelect().getVertex(), graphEditor.getSecondNodeSelect().getVertex());
        if(!pathExists){
            loggerInstance.printMessage(getClass().getName(), "Cannot find path between chosen vertices in this graph!", true);
        } else {
            List<MyRunnable> stepsActions = new ArrayList<>();

            for (var step : dijkstra.getSteps()) {
                var parent = step.getCurrent().getOriginal();
                var child = step.getChild().getOriginal();
                var newWeight = step.getWeight();

                var stepInterpreterInstance = this;

                switch (step.getType()) {
                    // Check appears after another CHECK or after MOVE or after UPDATE
                    case CHECK -> {
                        stepsActions.add(new MyRunnable() {
                            @Override
                            public void interrupt() {
                                if (stepInterpreterInstance.getLastType() != null && stepInterpreterInstance.getLastType() == StepType.MOVE) {
                                    graphPainter.stopPathAnimation();
                                }
                            }

                            @Override
                            public void run() {
                                if (stepInterpreterInstance.getLastType() != null) {
                                    // Cleaning
                                    switch (stepInterpreterInstance.getLastType()) {
                                        case UPDATE -> {
                                            graphPainter.removeMarkVertex(stepInterpreterInstance.getLastVertex(), DrawingType.UPDATE_FILL);
                                            graphPainter.removeMarkEdge(stepInterpreterInstance.getLastEdge(), false);
                                            graphPainter.removeMarkLabel(stepInterpreterInstance.getLastLabel(), false);
                                        }
                                        case CHECK -> {

                                            graphPainter.removeMarkVertex(stepInterpreterInstance.getLastVertex(), DrawingType.CHECK_STROKE);
                                            graphPainter.removeMarkEdge(stepInterpreterInstance.getLastEdge(), false);
                                            stepInterpreterInstance.getLastLabel().setText(stepInterpreterInstance.getLastWeight().split(" ")[0]);
                                            graphPainter.removeMarkLabel(stepInterpreterInstance.getLastLabel(), true);
                                        }
                                        // Doing nothing for MOVE
                                    }
                                }

                                stepInterpreterInstance.setLastType(step.getType());

                                graphPainter.addMarkVertex(child, DrawingType.CHECK_STROKE);
                                stepInterpreterInstance.setLastVertex(child);

                                var edge = graphPane.getGraph().getEdge(parent, child);
                                graphPainter.addMarkEdge(edge, false);
                                stepInterpreterInstance.setLastEdge(edge);

                                var label = graphPane.getVertexMap().get(child).getAlgLabel();
                                stepInterpreterInstance.setLastLabel(label);


                                var loggerMsg = "Checking vertex's '" + child.getData() + "' old path weight (" + label.getText() + ") with the new one (" + newWeight + "). ";

                                if (label.getText().equals("inf")) {
                                    label.setText(label.getText() + " > " + newWeight);
                                    loggerMsg += "New value is less that the old one. Applying new path.";
                                } else {
                                    var currentValue = Integer.parseInt(label.getText());
                                    if (currentValue < newWeight) {
                                        label.setText(currentValue + " < " + newWeight);
                                        loggerMsg += "New value is bigger that the old one. Leaving everything as it is.";
                                    } else if (currentValue > newWeight) {
                                        label.setText(currentValue + " > " + newWeight);
                                        loggerMsg += "New value is less that the old one. Applying new path.";
                                    } else {
                                        label.setText(currentValue + " = " + newWeight);
                                        loggerMsg += "New value is equal the old one. Leaving everything as it is.";
                                    }
                                }

                                stepInterpreterInstance.setLastWeight(label.getText());
                                graphPainter.addMarkLabel(label, true);
                                loggerInstance.printMessage(getClass().getName(), loggerMsg);
                                stepInterpreterInstance.setLastMsg(loggerMsg);
                            }
                        });
                    }
                    // Update appears after CHECK
                    case UPDATE -> {
                        stepsActions.add(new MyRunnable() {
                            @Override
                            public void interrupt() {

                            }

                            @Override
                            public void run() {
                                // Cleaning
                                if (stepInterpreterInstance.getLastType() != null && stepInterpreterInstance.getLastType() == StepType.CHECK) {
                                    graphPainter.removeMarkVertex(stepInterpreterInstance.getLastVertex(), DrawingType.CHECK_STROKE);
                                    graphPainter.removeMarkLabel(stepInterpreterInstance.getLastLabel(), true);
                                }
                                // Doing nothing for MOVE and UPDATE

                                stepInterpreterInstance.setLastType(step.getType());

                                graphPainter.addMarkVertex(child, DrawingType.UPDATE_FILL);
                                stepInterpreterInstance.setLastVertex(child);

                                var label = graphPane.getVertexMap().get(child).getAlgLabel();
                                label.setText(String.valueOf(newWeight));
                                graphPainter.addMarkLabel(label, false);
                                stepInterpreterInstance.setLastLabel(label);
                                stepInterpreterInstance.setLastWeight(label.getText());

                                var loggerMsg = "Checking showed, that new value/path (" + newWeight + ") should be applied to vertex '" + child.getData() + "'. Applying...";
                                loggerInstance.printMessage(getClass().getName(), loggerMsg);
                                stepInterpreterInstance.setLastMsg(loggerMsg);
                            }
                        });
                    }
                    // Move appears after CHECK or UPDATE
                    case MOVE -> {

                        stepsActions.add(new MyRunnable() {
                            @Override
                            public void interrupt() {

                            }

                            @Override
                            public void run() {
                                if (stepInterpreterInstance.getLastType() != null) {
                                    switch (stepInterpreterInstance.getLastType()) {
                                        case UPDATE -> {
                                            graphPainter.removeMarkVertex(stepInterpreterInstance.getLastVertex(), DrawingType.UPDATE_FILL);
                                            graphPainter.removeMarkEdge(stepInterpreterInstance.getLastEdge(), false);
                                            graphPainter.removeMarkLabel(stepInterpreterInstance.getLastLabel(), false);
                                        }
                                        case CHECK -> {
                                            graphPainter.removeMarkVertex(stepInterpreterInstance.getLastVertex(), DrawingType.CHECK_STROKE);
                                            graphPainter.removeMarkEdge(stepInterpreterInstance.getLastEdge(), false);
                                            stepInterpreterInstance.getLastLabel().setText(stepInterpreterInstance.getLastWeight().split(" ")[0]);
                                            graphPainter.removeMarkLabel(stepInterpreterInstance.getLastLabel(), true);
                                        }
                                        // Doing nothing for MOVE
                                    }

                                    if (stepInterpreterInstance.getLastPath() != null) {
                                        graphPainter.removePathMark(stepInterpreterInstance.getLastPath());
                                    }
                                }

                                stepInterpreterInstance.setLastType(step.getType());

                                var path = new ArrayList<String>();
                                path.add(step.getChild().getData());
                                var parentDVertex = step.getChild().getParent();

                                while (parentDVertex != null) {
                                    path.add(parentDVertex.getData());
                                    parentDVertex = parentDVertex.getParent();
                                }

                                Collections.reverse(path);

                                graphPainter.animatePath(path);
                                stepInterpreterInstance.setLastPath(path);

                                var loggerMsg = "";

                                if (graphEditor.getSecondNodeSelect().getVertex().equals(child)) {
                                    loggerMsg = "The shortest path to the end vertex was found! Moving there.";
                                } else
                                    loggerMsg = "Choosing between available vertices showed, that going to vertex '" + child.getData() + "' will result in the least path weight. Moving to this vertex.";
                                loggerInstance.printMessage(getClass().getName(), loggerMsg, graphEditor.getSecondNodeSelect().getVertex().equals(child), Alert.AlertType.INFORMATION);


                                stepInterpreterInstance.setLastMsg(loggerMsg);
                            }
                        });
                    }
                }
            }
            resourceLock = new ResourceLock(stepsActions, stepsDelay);
            initialised = true;
        }
    }

    /**
     * Pauses autoplay and makes step forward
     */
    public void makeStepForward() {
        pauseAutoPlay();
        System.out.println(resourceLock.currentTask.get());

        if (resourceLock.currentTask.get() + 1 <= resourceLock.tasks.size()) {
            resourceLock.tasks.get(resourceLock.currentTask.get()).run();
            resourceLock.currentTask.incrementAndGet();
        }

    }

    /**
     * Pauses autoplay and makes step backwards
     */
    public void makeStepBackwards() {

        pauseAutoPlay();

        System.out.println(resourceLock.currentTask.get());

        if (resourceLock.currentTask.get() - 1 >= 0) {
            resourceLock.currentTask.decrementAndGet(); // return to current

            if (lastTypeList.size() == 1) {
                cleanEverything();
            } else {
                switch (getBeforeLastType()) {
                    case CHECK -> {
                        switch (getLastType()) {
                            case UPDATE -> {
                                // here lastVertex and beforeLastVertex should be identical
                                if (!getLastVertex().equals(getBeforeLastVertex()))
                                    throw new RuntimeException("Last vertex and before last vertex are not equal. Before last: CHECK, last: UPDATE.");

                                graphPainter.removeMarkVertex(getLastVertex(), DrawingType.UPDATE_FILL);
                                graphPainter.removeMarkLabel(getLastLabel(), false);

                                graphPainter.addMarkVertex(getBeforeLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.addMarkLabel(getBeforeLastLabel(), true);

                                getLastLabel().setText(getBeforeLastWeight());

                                removeLastWeight();
                                removeLastVertex();
                                removeLastLabel();
                            }
                            case CHECK -> {

                                graphPainter.removeMarkEdge(getLastEdge(), false);
                                graphPainter.removeMarkVertex(getLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.removeMarkLabel(getLastLabel(), true);

                                getLastLabel().setText(getBeforeLastWeight().split(" ")[0]);

                                graphPainter.addMarkEdge(getBeforeLastEdge(), false);
                                graphPainter.addMarkVertex(getBeforeLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.addMarkLabel(getBeforeLastLabel(), true);

                                getBeforeLastLabel().setText(getBeforeLastWeight());

                                removeLastEdge();
                                removeLastVertex();
                                removeLastLabel();
                                removeLastWeight();
                            }
                            case MOVE -> {
                                graphPainter.removePathMark(getLastPath());

                                if (getBeforeLastPath() != null)
                                    graphPainter.animatePath(getBeforeLastPath());
                                graphPainter.addMarkEdge(getLastEdge(), false);
                                graphPainter.addMarkVertex(getLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.addMarkLabel(getLastLabel(), true);

                                getLastLabel().setText(getLastWeight());

                                removeLastPath();
                            }
                        }
                    }
                    case UPDATE -> {
                        switch (getLastType()) {
                            case CHECK -> {

                                graphPainter.removeMarkEdge(getLastEdge(), false);
                                graphPainter.removeMarkVertex(getLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.removeMarkLabel(getLastLabel(), true);

                                getLastLabel().setText(getLastWeight().split(" ")[0]);

                                graphPainter.addMarkEdge(getBeforeLastEdge(), false);
                                graphPainter.addMarkVertex(getBeforeLastVertex(), DrawingType.UPDATE_FILL);
                                graphPainter.addMarkLabel(getBeforeLastLabel(), false);

                                removeLastEdge();
                                removeLastVertex();
                                removeLastLabel();
                                removeLastWeight();
                            }
                            case MOVE -> {
                                graphPainter.removePathMark(getLastPath());

                                if (getBeforeLastPath() != null)
                                    graphPainter.animatePath(getBeforeLastPath());
                                graphPainter.addMarkEdge(getLastEdge(), false);
                                graphPainter.addMarkVertex(getLastVertex(), DrawingType.UPDATE_FILL);
                                graphPainter.addMarkLabel(getLastLabel(), false);

                                removeLastPath();
                            }
                        }
                    }
                    case MOVE -> {
                        switch (getLastType()) {
                            case CHECK -> {
                                graphPainter.removeMarkEdge(getLastEdge(), false);
                                graphPainter.removeMarkVertex(getLastVertex(), DrawingType.CHECK_STROKE);
                                graphPainter.removeMarkLabel(getLastLabel(), true);

                                getLastLabel().setText(getLastWeight().split(" ")[0]);

                                removeLastEdge();
                                removeLastVertex();
                                removeLastLabel();
                                removeLastWeight();
                            }

                            case MOVE -> {
                                graphPainter.removePathMark(getLastPath());

                                if (getBeforeLastPath() != null)
                                    graphPainter.animatePath(getBeforeLastPath());

                                removeLastPath();
                            }
                        }
                    }
                }

                loggerInstance.printMessage(getClass().getName(), getLastMsg());
                removeLastMsg();
            }

            removeLastType();
        }

    }

    /**
     * Starts autoplay
     */
    public void startAutoPlay() {
        threadA = new ThreadA(resourceLock);
        threadB = new ThreadB(resourceLock);

        threadA.start();
        threadB.start();

        isPaused = false;
    }

    /**
     * Pauses autoplay
     */
    public void pauseAutoPlay() {
        resourceLock.working = false;
        isPaused = true;
    }

    /**
     * Resumes autoplay after pause
     */
    public void resumeAutoPlay() {
        if (isPaused) {
            resourceLock.working = true;

            threadA = new ThreadA(resourceLock);
            threadB = new ThreadB(resourceLock);

            threadA.start();
            threadB.start();

            isPaused = false;
        }
    }

    /**
     * Stops engine completely. Resets current drawing and step position
     */
    public void stop() {
        if (threadA != null)
            threadA.interrupt();
        if (threadB != null)
            threadB.interrupt();

        resourceLock.working = true;
        resourceLock.currentTask.set(0);
        resourceLock.flag = 0;

        cleanEverything();

        graphEditor.clearAlgLabels();
    }

    /**
     * Cleans every animation (drawing) on screen and resets steps data
     */
    private void cleanEverything() {
        if (getLastType() != null) {
            switch (getLastType()) {
                case UPDATE -> {
                    graphPainter.removeMarkVertex(getLastVertex(), DrawingType.UPDATE_FILL);
                    graphPainter.removeMarkEdge(getLastEdge(), false);
                    graphPainter.removeMarkLabel(getLastLabel(), false);
                }
                case CHECK -> {
                    graphPainter.removeMarkVertex(getLastVertex(), DrawingType.CHECK_STROKE);
                    graphPainter.removeMarkEdge(getLastEdge(), false);
                    getLastLabel().setText(getLastWeight().split(" ")[0]);
                    graphPainter.removeMarkLabel(getLastLabel(), true);
                }
                // Doing nothing for MOVE
            }

            if (getLastPath() != null)
                graphPainter.removePathMark(getLastPath());

        }

        lastEdgeList.clear();
        lastLabelList.clear();
        lastPathList.clear();
        lastVertexList.clear();
        lastWeightList.clear();
        lastTypeList.clear();
    }

    public StepType getLastType() {
        if (lastTypeList.size() == 0) {
            return null;
        }
        return lastTypeList.get(lastTypeList.size() - 1);
    }

    public StepType getBeforeLastType() {
        if (lastTypeList.size() < 2) {
            return null;
        }
        return lastTypeList.get(lastTypeList.size() - 2);
    }

    public void setLastType(StepType lastType) {
        lastTypeList.add(lastType);
    }

    public void removeLastType() {
        if (lastTypeList.size() != 0) {
            lastTypeList.remove(lastTypeList.size() - 1);
        }
    }

    public Edge getLastEdge() {
        if (lastEdgeList.size() == 0) {
            return null;
        }
        return lastEdgeList.get(lastEdgeList.size() - 1);
    }

    public Edge getBeforeLastEdge() {
        if (lastEdgeList.size() < 2) {
            return null;
        }
        return lastEdgeList.get(lastEdgeList.size() - 2);
    }

    public void setLastEdge(Edge lastEdge) {
        lastEdgeList.add(lastEdge);
    }

    public void removeLastEdge() {
        if (lastEdgeList.size() != 0) {
            lastEdgeList.remove(lastEdgeList.size() - 1);
        }
    }

    public Vertex getLastVertex() {
        if (lastVertexList.size() == 0) {
            return null;
        }
        return lastVertexList.get(lastVertexList.size() - 1);
    }

    public Vertex getBeforeLastVertex() {
        if (lastVertexList.size() < 2) {
            return null;
        }
        return lastVertexList.get(lastVertexList.size() - 2);
    }

    public void setLastVertex(Vertex lastVertex) {
        lastVertexList.add(lastVertex);
    }

    public void removeLastVertex() {
        if (lastVertexList.size() != 0) {
            lastVertexList.remove(lastVertexList.size() - 1);
        }
    }

    public Label getLastLabel() {
        if (lastLabelList.size() == 0) {
            return null;
        }
        return lastLabelList.get(lastLabelList.size() - 1);
    }

    public Label getBeforeLastLabel() {
        if (lastLabelList.size() < 2) {
            return null;
        }
        return lastLabelList.get(lastLabelList.size() - 2);
    }

    public void setLastLabel(Label lastLabel) {
        lastLabelList.add(lastLabel);
    }

    public void removeLastLabel() {
        if (lastLabelList.size() != 0) {
            lastLabelList.remove(lastLabelList.size() - 1);
        }
    }

    public String getLastWeight() {
        if (lastWeightList.size() == 0) {
            return null;
        }
        return lastWeightList.get(lastWeightList.size() - 1);
    }

    public String getBeforeLastWeight() {
        if (lastWeightList.size() < 2) {
            return null;
        }
        return lastWeightList.get(lastWeightList.size() - 2);
    }

    public void setLastWeight(String lastWeight) {
        lastWeightList.add(lastWeight);
    }

    public void removeLastWeight() {
        if (lastWeightList.size() != 0) {
            lastWeightList.remove(lastWeightList.size() - 1);
        }
    }

    public List<String> getLastPath() {
        if (lastPathList.size() == 0) {
            return null;
        }
        return lastPathList.get(lastPathList.size() - 1);
    }

    public List<String> getBeforeLastPath() {
        if (lastPathList.size() < 2) {
            return null;
        }
        return lastPathList.get(lastPathList.size() - 2);
    }

    public void setLastPath(List<String> lastPath) {
        lastPathList.add(lastPath);
    }

    public void removeLastPath() {
        if (lastPathList.size() != 0) {
            lastPathList.remove(lastPathList.size() - 1);
        }
    }

    public String getLastMsg() {
        if (lastMsgList.size() == 0) {
            return null;
        }
        return lastMsgList.get(lastMsgList.size() - 1);
    }

    public String getBeforeLastMsg() {
        if (lastMsgList.size() < 2) {
            return null;
        }
        return lastMsgList.get(lastMsgList.size() - 2);
    }

    public void setLastMsg(String lastPath) {
        lastMsgList.add(lastPath);
    }

    public void removeLastMsg() {
        if (lastMsgList.size() != 0) {
            lastMsgList.remove(lastMsgList.size() - 1);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isPathExists() {
        return pathExists;
    }
}
