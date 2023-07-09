package ru.etu.graphview.base;


import javafx.scene.control.TextField;

/**
 * This interface gives some methods to work with labels and textFields.
 *
 * @see Label
 */
public interface LabelledNode {

    /**
     * Saves label and setups its coordinates for proper view.
     *
     * @param label label link
     */
    void setLabel(Label label);

    /**
     * Gets label link, stored inside
     *
     * @return label link, stored inside
     */
    Label getLabel();

    /**
     * Saves text field and setups its coordinates for proper view.
     *
     * @param labelField text field link
     */
    void setLabelField(TextField labelField);

    /**
     * Gets text field link, stored inside
     *
     * @return text field link, stored inside
     */
    TextField getLabelField();
}
