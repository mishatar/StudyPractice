package ru.etu.graphview.base;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import ru.etu.graph.Vertex;
import ru.etu.graphview.styling.StyleEngine;

/**
 * Implementation of FXVertex interface with Circle element.
 *
 * @see FXVertex
 */
public class FXVertexNode extends Circle implements FXVertex {

    private final StyleEngine styleEngine;

    private Vertex vertex;

    private Label label;
    private Label algLabel;
    private TextField labelField;
    private boolean isDragging;

    // Used, when vertex creation will be done after circle creation.
    public FXVertexNode(double x, double y, double radius) {
        super(x, y, radius);

        enableDrag();

        styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("vertex");

        this.label = null;
        this.isDragging = false;
    }

    public FXVertexNode(Vertex vertex, double x, double y, double radius) {
        super(x, y, radius);

        enableDrag();

        styleEngine = new StyleEngine(this);
        styleEngine.setStyleClass("vertex");

        this.vertex = vertex;
        this.label = null;
        this.isDragging = false;
    }

    /**
     * Gets position of the center of this vertex view
     *
     * @return position of the center of this vertex view
     */
    public Point2D getPosition() {
        return new Point2D(getCenterX(), getCenterY());
    }

    @Override
    public void setPosition(double x, double y) {
        if (isDragging) {
            return;
        }

        setCenterX(x);
        setCenterY(y);
    }

    /**
     * Enables user drag
     */
    public void enableDrag() {
        class Point {
            double x, y;

            public Point(double x, double y) {
                this.x = x;
                this.y = y;
            }
        }

        final Point dragDelta = new Point(0, 0);

        setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                dragDelta.x = getCenterX() - event.getX();
                dragDelta.y = getCenterY() - event.getY();

                getScene().setCursor(Cursor.MOVE);
                isDragging = true;

                event.consume();
            }
        });

        setOnMouseReleased(event -> {
            getScene().setCursor(Cursor.HAND);
            isDragging = false;

            event.consume();
        });

        setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double newX = event.getX() + dragDelta.x;
                double x = boundCenterCoordinate(newX, 0, getParent().getLayoutBounds().getWidth());
                setCenterX(x);

                double newY = event.getY() + dragDelta.y;
                double y = boundCenterCoordinate(newY, 0, getParent().getLayoutBounds().getHeight());
                setCenterY(y);
            }
        });

        setOnMouseEntered(event -> {
            if (!event.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }

        });

        setOnMouseExited(event -> {
            if (!event.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    /**
     * Disables user drag
     */
    public void disableDrag() {
        setOnMousePressed(event -> {
        });

        setOnMouseReleased(event -> {
        });

        setOnMouseDragged(event -> {
        });

        setOnMouseEntered(event -> {
        });

        setOnMouseExited(event -> {
        });
    }

    /**
     * Sets value to fit in boundaries: (min + radius; max - radius)
     *
     * @param value - current value
     * @param min   - min value
     * @param max   - max value
     * @return bound value
     */
    private double boundCenterCoordinate(double value, double min, double max) {
        double radius = getRadius();

        if (value < min + radius) {
            return min + radius;
        } else if (value > max - radius) {
            return max - radius;
        } else {
            return value;
        }
    }

    @Override
    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    @Override
    public Vertex getVertex() {
        return vertex;
    }

    @Override
    public void setLabel(Label label) {
        this.label = label;

        label.setStyleClass("vertex-label");

        label.xProperty().bind(centerXProperty().subtract(label.getLayoutBounds().getWidth() / 2.0));
        label.yProperty().bind(centerYProperty().add(getRadius() + label.getLayoutBounds().getHeight()));
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public void setAlgLabel(Label algLabel) {
        this.algLabel = algLabel;

        algLabel.setStyleClass("vertex-alg-label");

        algLabel.xProperty().bind(centerXProperty().subtract(algLabel.getLayoutBounds().getWidth() / 2.0));
        algLabel.yProperty().bind(centerYProperty().subtract(getRadius() + algLabel.getLayoutBounds().getHeight() / 2.0));
    }

    @Override
    public Label getAlgLabel() {
        return algLabel;
    }

    @Override
    public void setLabelField(TextField labelField) {
        this.labelField = labelField;
        labelField.translateXProperty().bind(centerXProperty().subtract(70 / 2.0));
        labelField.translateYProperty().bind(centerYProperty().add(getRadius() + 8));
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
