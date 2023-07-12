package ru.etu.graphview.drawing;

import ru.etu.graph.Edge;
import ru.etu.graph.Vertex;
import ru.etu.graphview.GraphPane;
import ru.etu.graphview.base.FXEdge;
import ru.etu.graphview.base.FXVertexNode;
import ru.etu.graphview.base.Label;
import ru.etu.graphview.drawing.multithreading.MyRunnable;
import ru.etu.graphview.drawing.multithreading.ResourceLock;
import ru.etu.graphview.drawing.multithreading.ThreadA;
import ru.etu.graphview.drawing.multithreading.ThreadB;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all major changes in GraphPane components. Implements singleton pattern
 */
public class GraphPainter {
    private static GraphPainter instance;

    private final int PATH_ANIMATION_DELAY = 200;

    private ResourceLock loopResource; // Resources for threads
    private ThreadA threadA;
    private ThreadB threadB;

    private GraphPane graphPane;

    /**
     * Gets instance of this class (with initialising if necessary )
     *
     * @param graphPane GraphPane link
     * @return GraphPainter instance
     */
    public static GraphPainter getInstance(GraphPane graphPane) {
        if (instance == null) {
            instance = new GraphPainter(graphPane);
        }

        return instance;
    }

    private GraphPainter(GraphPane graphPane) {
        this.graphPane = graphPane;
    }

    /**
     * Animates path by coloring edges one by one with certain delay
     *
     * @param path list of vertex names on the path
     */
    public void animatePath(List<String> path) {

        var eventsList = new ArrayList<MyRunnable>();

        eventsList.add(new MyRunnable() {
            @Override
            public void interrupt() {

            }

            @Override
            public void run() {
                addMarkVertex(graphPane.getGraph().getVertex(path.get(0)), DrawingType.PATH_STROKE);
            }
        });

        for (int i = 0; i < path.size() - 1; i++) {

            int finalI = i;
            eventsList.add(new MyRunnable() {
                @Override
                public void interrupt() {

                }

                @Override
                public void run() {
                    var edge = graphPane.getGraph().getEdge(path.get(finalI), path.get(finalI + 1));

                    addMarkEdge(edge, true);
                    addMarkVertex(graphPane.getGraph().getVertex(path.get(finalI + 1)), DrawingType.PATH_STROKE);
                }
            });

        }


        loopResource = new ResourceLock(eventsList, PATH_ANIMATION_DELAY);

        threadA = new ThreadA(loopResource);
        threadB = new ThreadB(loopResource);

        threadA.start();
        threadB.start();
    }

    /**
     * Removes marking of the path, created by animatePath(...)
     *
     * @param path list of vertex names on the path
     */
    public void removePathMark(List<String> path) {
        stopPathAnimation();
        for (int i = 0; i < path.size() - 1; i++) {
            var edge = graphPane.getGraph().getEdge(path.get(i), path.get(i + 1));


            removeMarkEdge(edge, true);
            removeMarkVertex(graphPane.getGraph().getVertex(path.get(i)), DrawingType.PATH_STROKE);
        }
        removeMarkVertex(graphPane.getGraph().getVertex(path.get(path.size() - 1)), DrawingType.PATH_STROKE);
    }

    /**
     * Stops running animation
     */
    public void stopPathAnimation() {
        threadA.interrupt();
        threadB.interrupt();
    }

    /**
     * Marks vertex according to the drawing type
     *
     * @param vertex      vertex link
     * @param drawingType type of drawing
     */
    public void addMarkVertex(Vertex vertex, DrawingType drawingType) {
        var fxVertex = graphPane.getVertexMap().get(vertex);

        switch (drawingType) {
            case PATH_STROKE -> {
                fxVertex.addStyleClass("vertex-path");
            }
            case CHECK_STROKE -> {
                fxVertex.addStyleClass("vertex-check");
            }
            case UPDATE_FILL -> {
                fxVertex.addStyleClass("vertex-update");
            }
        }

    }

    /**
     * Removes vertex marking according to the drawing type
     *
     * @param vertex      vertex link
     * @param drawingType type of drawing was used for marking
     */
    public void removeMarkVertex(Vertex vertex, DrawingType drawingType) {
        var fxVertex = graphPane.getVertexMap().get(vertex);

        switch (drawingType) {
            case PATH_STROKE -> {
                fxVertex.removeStyleClass("vertex-path");
            }
            case CHECK_STROKE -> {
                fxVertex.removeStyleClass("vertex-check");
            }
            case UPDATE_FILL -> {
                fxVertex.removeStyleClass("vertex-update");
            }
        }
    }

    /**
     * Marks edge
     *
     * @param edge   edge link
     * @param isPath is marking for path mark
     */
    public void addMarkEdge(Edge edge, boolean isPath) {
        var fxEdge = graphPane.getEdgeMap().get(edge);

        if (isPath) {
            fxEdge.addStyleClass("edge-path");
            if (fxEdge.getArrow() != null)
                fxEdge.getArrow().addStyleClass("arrow-path");
        } else {
            fxEdge.addStyleClass("edge-check");
            if (fxEdge.getArrow() != null)
                fxEdge.getArrow().addStyleClass("arrow-check");
        }

    }

    /**
     * Removes edge marking
     *
     * @param edge   edge link
     * @param isPath was marking for path mark
     */
    public void removeMarkEdge(Edge edge, boolean isPath) {
        var fxEdge = graphPane.getEdgeMap().get(edge);

        if (isPath) {
            fxEdge.removeStyleClass("edge-path");
            if (fxEdge.getArrow() != null)
                fxEdge.getArrow().removeStyleClass("arrow-path");
        } else {
            fxEdge.removeStyleClass("edge-check");
            if (fxEdge.getArrow() != null)
                fxEdge.getArrow().removeStyleClass("arrow-check");
        }
    }

    /**
     * Adds label mark
     *
     * @param label      label link
     * @param isChecking is marking for CHECK state
     */
    public void addMarkLabel(Label label, boolean isChecking) {
        if (isChecking) {
            label.addStyleClass("checking-label");
        } else {
            label.addStyleClass("updating-label");
        }
    }

    /**
     * Removes label mark
     *
     * @param label      label link
     * @param isChecking was marking for CHECK state
     */
    public void removeMarkLabel(Label label, boolean isChecking) {
        if (isChecking) {
            label.removeStyleClass("checking-label");
        } else {
            label.removeStyleClass("updating-label");
        }
    }
}
