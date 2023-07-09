package ru.etu.graphview.base;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.etu.graphview.styling.Stylable;
import ru.etu.graphview.styling.StyleEngine;

/**
 * This class represent Label to use in vertex and edge views.
 * Implements Stylable interface for easier styling.
 *
 * @see Stylable
 * @see LabelledNode
 */
public class Label extends Text implements Stylable {
    private final StyleEngine styleEngine;

    public Label() {
        this(0, 0, "");
    }

    public Label(String text) {
        this(0, 0, text);
    }

    public Label(double x, double y, String text) {
        super(x, y, "");
        if (text.length() > 4) {
            setText(text.substring(0, 4) + "...");
            Tooltip tooltip = new Tooltip(text);
            tooltip.setShowDelay(Duration.millis(400));
            tooltip.setHideDelay(Duration.millis(200));
            Tooltip.install(this, tooltip);
        } else {
            setText(text);
        }
        styleEngine = new StyleEngine(this);
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
