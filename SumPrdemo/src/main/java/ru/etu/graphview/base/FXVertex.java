package ru.etu.graphview.base;

import ru.etu.graph.Vertex;
import ru.etu.graphview.styling.Stylable;

/**
 * Base interface for Vertex View implementation.
 * Can be labeled and conveniently styled.
 *
 * @see Stylable
 * @see LabelledNode
 */
public interface FXVertex extends Stylable, LabelledNode {

    /**
     * Sets vertex to its view
     *
     * @param vertex vertex link
     */
    void setVertex(Vertex vertex);

    /**
     * Method, that gets stored vertex, represented by this view
     *
     * @return vertex link, stored inside
     */
    Vertex getVertex();

    /**
     * Sets position for the vertex
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    void setPosition(double x, double y);

    /**
     * Returns radius of vertex
     *
     * @return radius
     */
    double getRadius();

    // These two methods are not in the LabelledNode interface, because they are used only in FXVertex for algorithm data display

    /**
     * Saves label for algorithm and setups its coordinates for proper view.
     *
     * @param algLabel label for algorithm link
     */
    void setAlgLabel(Label algLabel);

    /**
     * Gets label for algorithm link, stored inside
     *
     * @return label for algorithm link, stored inside
     */
    Label getAlgLabel();
}
