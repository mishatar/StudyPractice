package ru.etu.graphview;

/**
 * This class stores some view properties for the graph.
 */
public class GraphViewProperties {

    /**
     * Radius of vertices in pixels
     */
    private double verticesRadius = 15;

    /**
     * Max angle of bending for curve lines
     */
    private double curveEdgeAngle = 30;

    /**
     * Sets if arrows must be placed
     */
    private boolean needArrows = true;

    /**
     * Sets if double edges can be placed
     */
    private boolean needDoubleEdges = true;

    /**
     * Arrow size in pixels
     */
    private int arrowSize = 9;

    /**
     * Minimum scale factor. Must be a multiple of scaleStep
     */
    private double minScale = 0.25;

    /**
     * Maximum scale factor. Must be a multiple of scaleStep
     */
    private double maxScale = 3;

    /**
     * Step of scaling
     */
    private double scaleStep = 0.25;

    public double getVerticesRadius() {
        return verticesRadius;
    }

    public void setVerticesRadius(double verticesRadius) {
        this.verticesRadius = verticesRadius;
    }

    public double getCurveEdgeAngle() {
        return curveEdgeAngle;
    }

    public void setCurveEdgeAngle(double curveEdgeAngle) {
        this.curveEdgeAngle = curveEdgeAngle;
    }

    public boolean isNeedArrows() {
        return needArrows;
    }

    public void setNeedArrows(boolean needArrows) {
        this.needArrows = needArrows;
    }

    public int getArrowSize() {
        return arrowSize;
    }

    public void setArrowSize(int arrowSize) {
        this.arrowSize = arrowSize;
    }

    public boolean isNeedDoubleEdges() {
        return needDoubleEdges;
    }

    public void setNeedDoubleEdges(boolean needDoubleEdges) {
        this.needDoubleEdges = needDoubleEdges;
    }

    public double getMinScale() {
        return minScale;
    }

    public void setMinScale(double minScale) {
        this.minScale = minScale;
    }

    public double getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    }

    public double getScaleStep() {
        return scaleStep;
    }

    public void setScaleStep(double scaleStep) {
        this.scaleStep = scaleStep;
    }
}
