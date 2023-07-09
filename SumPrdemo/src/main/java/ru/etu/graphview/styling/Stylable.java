package ru.etu.graphview.styling;

/**
 * Interface that helps to easily apply styles to Shapes
 *
 * @see StyleEngine
 */
public interface Stylable {

    /**
     * Sets new style. Old style will be deleted.
     *
     * @param css new style in css format
     */
    void setStyleCss(String css);

    /**
     * Sets new style class. Old style classes will be deleted.
     *
     * @param cssClassName new style class names
     */
    void setStyleClass(String cssClassName);

    /**
     * Adds new style class. Old style classes will not be deleted.
     *
     * @param cssClassName new style class names
     */
    void addStyleClass(String cssClassName);

    /**
     * Removes style class
     *
     * @param cssClassName style class name
     */
    boolean removeStyleClass(String cssClassName);


}
