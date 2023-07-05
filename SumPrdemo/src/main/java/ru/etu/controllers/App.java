package ru.etu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.ResourceBundle;

public class App implements Initializable {

    // Buttons
    public Button prevBtn;
    public Button nextBtn;
    public ToggleButton pauseBtn;
    public ToggleButton playBtn;
    public Button stopBtn;
    public ToggleButton moveBtn;
    public ToggleButton vertexBtn;
    public ToggleButton edgeBtn;
    public ToggleButton chooseBtn;
    public Button clearBtn;

    // Menus and menu items
    public MenuItem saveMeMenuItem;
    public MenuItem saveAsMenuItem;
    public MenuItem changeMenuItem;
    public Menu playMenu;
    public Menu settingsMenu;
    public MenuItem exitMenuItem;
    public MenuItem prevMenuItem;
    public MenuItem nextMenuItem;
    public MenuItem pauseMenuItem;
    public MenuItem playMenuItem;
    public MenuItem stopMenuItem;
    public MenuItem moveMenuItem;
    public MenuItem vertexMenuItem;
    public MenuItem edgeMenuItem;
    public MenuItem chooseMenuItem;
    public MenuItem clearMenuItem;

    // Other objects
    public BorderPane contentBorderPane;
    public AnchorPane mainPane;
    public MenuBar menuBar;
    public Pane graphPanePane;
    private boolean isLeft = true;
    public Node top;
    public Node left;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        /*
        PANELS INITIALIZATION
         */
        top = contentBorderPane.getTop();
        left = contentBorderPane.getLeft();
        contentBorderPane.setTop(null);

    }

    @FXML
    private void changeMode() {

        if (!isLeft) {
            // Switching panels
            contentBorderPane.setTop(null);
            contentBorderPane.setLeft(left);
            isLeft = true;

        } else {
            // Play mode
            // Switching panels
            contentBorderPane.setTop(top);
            contentBorderPane.setLeft(null);
            isLeft = false;
        }
    }

}
