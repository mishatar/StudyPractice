package ru.etu.graphview.base;

import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ru.etu.graph.Edge;
import ru.etu.graphview.styling.StyleEngine;

/**
 * Implementation of FXEdge interface with strait line.
 *
 * @see FXEdge
 */
public class FXEdgeLine extends Line implements FXEdge {

    private final StyleEngine styleEngine;

    private Edge edge;

    private final FXVertexNode inboundVertex;
    private final FXVertexNode outboundVertex;

    private Label label = null;
    private TextField labelField = null;
    private Arrow arrow = null;

    // Used, when edge creation will be done after line creation.
    public FXEdgeLine(FXVertexNode outboundVertex, FXVertexNode inboundVertex) {
        if (inboundVertex == null || outboundVertex == null) {
            throw new IllegalArgumentException("Edge should have both inbound and outbound vertex.");
        }

        this.inboundVertex = inboundVertex;
        this.outboundVertex = outboundVertex;

        this.startXProperty().bind(outboundVertex.centerXProperty());
        this.startYProperty().bind(outboundVertex.centerYProperty());

        this.endXProperty().bind(inboundVertex.centerXProperty());
        this.endYProperty().bind(inboundVertex.centerYProperty());

        styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("edge");
    }

    public FXEdgeLine(Edge edge, FXVertexNode outboundVertex, FXVertexNode inboundVertex) {
        if (inboundVertex == null || outboundVertex == null) {
            throw new IllegalArgumentException("Edge should have both inbound and outbound vertex.");
        }

        this.edge = edge;

        this.inboundVertex = inboundVertex;
        this.outboundVertex = outboundVertex;

        this.startXProperty().bind(outboundVertex.centerXProperty());
        this.startYProperty().bind(outboundVertex.centerYProperty());

        this.endXProperty().bind(inboundVertex.centerXProperty());
        this.endYProperty().bind(inboundVertex.centerYProperty());

        styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("edge");
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
                endYProperty().subtract(startYProperty()),
                endXProperty().subtract(startXProperty())
        )));

        arrow.getTransforms().add(rotation);

        arrow.getTransforms().add(new Translate(-outboundVertex.getRadius(), 0));
    }

    @Override
    public Arrow getArrow() {
        return arrow;
    }

    @Override
    public void setLabel(Label label) {
        this.label = label;

        label.setStyleClass("edge-label");

        label.xProperty().bind(startXProperty().add(endXProperty()).divide(2).subtract(label.getLayoutBounds().getWidth() / 2));
        label.yProperty().bind(startYProperty().add(endYProperty()).divide(2).add(label.getLayoutBounds().getHeight() / 1.5));
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public void setLabelField(TextField labelField) {
        this.labelField = labelField;
        labelField.translateXProperty().bind(startXProperty().add(endXProperty()).divide(2).subtract(70 / 2));
        labelField.translateYProperty().bind(startYProperty().add(endYProperty()).divide(2).add(15 / 1.5));
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
