package ru.etu.graphview.styling;

import javafx.scene.shape.Shape;

/**
 * General implementation of Stylable interface for all objects that needed it.
 *
 * @see Stylable
 */
public class StyleEngine implements Stylable {

    private final Shape element;

    public StyleEngine(Shape element) {
        this.element = element;
    }

    @Override
    public void setStyleCss(String css) {
        element.setStyle(css);
    }

    @Override
    public void setStyleClass(String cssClassName) {
        element.getStyleClass().clear();
        element.setStyle(null);
        element.getStyleClass().add(cssClassName);
    }

    @Override
    public void addStyleClass(String cssClassName) {
        element.getStyleClass().add(cssClassName);
    }

    @Override
    public boolean removeStyleClass(String cssClassName) {
        return element.getStyleClass().remove(cssClassName);
    }
}
