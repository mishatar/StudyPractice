package ru.etu.graphview.base;

import ru.etu.graph.Edge;
import ru.etu.graphview.styling.Stylable;

/**
 * Base interface for Edge View implementation.
 * Can be labeled and conveniently styled.
 *
 * @see LabelledNode
 * @see Stylable
 */
public interface FXEdge extends LabelledNode, Stylable {

    /**
     * Method, that gets stored edge, represented by this view
     *
     * @return edge link, stored inside
     */
    Edge getEdge();

    /**
     * Sets edge to be represented by this view
     *
     * @param edge edge link
     */
    void setEdge(Edge edge);

    /**
     * Saves arrow and setups its coordinates and rotation for proper view.
     *
     * @param arrow arrow link
     */
    void setArrow(Arrow arrow);

    /**
     * Gets arrow link, stored inside
     *
     * @return arrow link, stored inside
     */
    Arrow getArrow();


}
