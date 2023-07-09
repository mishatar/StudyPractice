package ru.etu.graphview.base;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import ru.etu.graphview.styling.Stylable;
import ru.etu.graphview.styling.StyleEngine;

/**
 * Class for Arrow View. Basically it is two lines that was drawn manually.
 * This class implements Stylable interface. Therefore, its style can be changed conveniently by methods from this interface.
 *
 * @see Stylable
 */
public class Arrow extends Path implements Stylable {

    /**
     * Style engine for simpler implementation of Stylable interface
     */
    private final StyleEngine styleEngine;

    public Arrow(double size) {
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, size));
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(-size, -size));

        styleEngine = new StyleEngine(this);
        setStyleClass("arrow");
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
