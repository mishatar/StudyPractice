package ru.etu.graphview.base;

import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.shape.QuadCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ru.etu.graph.Edge;
import ru.etu.graphview.styling.StyleEngine;

/**
 * Implementation of FXEdge interface with curve line.
 *
 * @see FXEdge
 */
public class FXEdgeCurve extends QuadCurve implements FXEdge {

    private double angle;
    private final int bendingCoefficient; // Can be either -1 or 1. Sets side of bending

    private final StyleEngine styleEngine;

    private Edge edge;

    private final FXVertexNode inboundVertex;
    private final FXVertexNode outboundVertex;

    private Label label = null;
    private TextField labelField = null;
    private Arrow arrow = null;


    // Used, when edge creation will be done after curve creation.
    public FXEdgeCurve(FXVertexNode outboundVertex, FXVertexNode inboundVertex, int bendingCoefficient, double angle) {
        if (inboundVertex == null || outboundVertex == null) {
            throw new IllegalArgumentException("Edge should have both inbound and outbound vertex.");
        }

        this.angle = angle;
        this.bendingCoefficient = bendingCoefficient;

        this.inboundVertex = inboundVertex;
        this.outboundVertex = outboundVertex;

        this.startXProperty().bind(outboundVertex.centerXProperty());
        this.startYProperty().bind(outboundVertex.centerYProperty());

        this.endXProperty().bind(inboundVertex.centerXProperty());
        this.endYProperty().bind(inboundVertex.centerYProperty());

        update();
        enableListeners();

        this.styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("curved-edge");

    }

    public FXEdgeCurve(Edge edge, FXVertexNode outboundVertex, FXVertexNode inboundVertex, int bendingCoefficient, double angle) {
        if (inboundVertex == null || outboundVertex == null) {
            throw new IllegalArgumentException("Edge should have both inbound and outbound vertex.");
        }

        this.angle = angle;
        this.bendingCoefficient = bendingCoefficient;

        if (bendingCoefficient != 1 && bendingCoefficient != -1) {
            throw new IllegalArgumentException("bendingCoefficient should be either 1 or -1.");
        }

        this.edge = edge;

        this.inboundVertex = inboundVertex;
        this.outboundVertex = outboundVertex;

        this.startXProperty().bind(outboundVertex.centerXProperty());
        this.startYProperty().bind(outboundVertex.centerYProperty());

        this.endXProperty().bind(inboundVertex.centerXProperty());
        this.endYProperty().bind(inboundVertex.centerYProperty());

        update();
        enableListeners();

        this.styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("curved-edge");

    }

    /**
     * This method updates control point with new start and finish points.
     */
    private void update() {
        Point2D midPoint = new Point2D((outboundVertex.getCenterX() + inboundVertex.getCenterX()) / 2,
                (outboundVertex.getCenterY() + inboundVertex.getCenterY()) / 2);

        Point2D startPoint = new Point2D(outboundVertex.getCenterX(), outboundVertex.getCenterY());
        Point2D endPoint = new Point2D(inboundVertex.getCenterX(), inboundVertex.getCenterY());


        double distance = startPoint.distance(endPoint);

        var angle1 = angle - (distance * angle / 1700);

        Point2D midPoint1 = Utils.rotate(midPoint, (bendingCoefficient == 1) ? startPoint : endPoint, angle1 * bendingCoefficient);
        Point2D midPoint2 = Utils.rotate(midPoint, (bendingCoefficient == 1) ? endPoint : startPoint, -angle1 * bendingCoefficient);


        setControlX((midPoint1.getX() + midPoint2.getX()) / 2);
        setControlY((midPoint1.getY() + midPoint2.getY()) / 2);
    }

    /**
     * Sets calling update() method, when any property was changed.
     */
    private void enableListeners() {
        this.startXProperty().addListener((ov, t, t1) -> update());
        this.startYProperty().addListener((ov, t, t1) -> update());
        this.endXProperty().addListener((ov, t, t1) -> update());
        this.endYProperty().addListener((ov, t, t1) -> update());
    }

    @Override
    public Edge getEdge() {
        return edge;
    }

    @Override
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    @Override
    public void setArrow(Arrow arrow) {
        this.arrow = arrow;

        arrow.translateXProperty().bind(endXProperty());
        arrow.translateYProperty().bind(endYProperty());

        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(translateXProperty());
        rotation.pivotYProperty().bind(translateYProperty());
        rotation.angleProperty().bind(Utils.toDegrees(Utils.atan2(
                endYProperty().subtract(controlYProperty()),
                endXProperty().subtract(controlXProperty())
        )));

        arrow.getTransforms().add(rotation);

        arrow.getTransforms().add(new Translate(-inboundVertex.getRadius(), 0));
    }

    @Override
    public Arrow getArrow() {
        return arrow;
    }

    @Override
    public void setLabel(Label label) {
        this.label = label;

        label.setStyleClass("edge-label");

        label.xProperty().bind(controlXProperty().add(controlXProperty().add(startXProperty().add(endXProperty()).divide(2)).divide(2)).divide(2).subtract(label.getLayoutBounds().getWidth() / 2));
        label.yProperty().bind(controlYProperty().add(controlYProperty().add(startYProperty().add(endYProperty()).divide(2)).divide(2)).divide(2).add(label.getLayoutBounds().getHeight() / 2));
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public void setLabelField(TextField labelField) {
        this.labelField = labelField;

        labelField.translateXProperty().bind(controlXProperty().add(controlXProperty().add(startXProperty().add(endXProperty()).divide(2)).divide(2)).divide(2).subtract(70 / 2));
        labelField.translateYProperty().bind(controlYProperty().add(controlYProperty().add(startYProperty().add(endYProperty()).divide(2)).divide(2)).divide(2).add(15 / 2));
    }

    @Override
    public TextField getLabelField() {
        return labelField;
    }

    @Override
    public void setStyleCss(String css) {
        styleEngine.setStyleCss(css);
    }

    @Override
    public void setStyleClass(String cssClassName) {
        styleEngine.setStyleClass(cssClassName);
    }

    @Override
    public void addStyleClass(String cssClassName) {
        styleEngine.addStyleClass(cssClassName);
    }

    @Override
    public boolean removeStyleClass(String cssClassName) {
        return styleEngine.removeStyleClass(cssClassName);
    }
}
