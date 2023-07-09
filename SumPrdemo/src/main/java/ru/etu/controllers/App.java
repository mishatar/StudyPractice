package ru.etu.controllers;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ru.etu.graph.DirectedGraphList;
import ru.etu.graph.Graph;
import ru.etu.graph.GraphList;
import ru.etu.graphview.GraphPane;
import ru.etu.graphview.GraphViewProperties;
import ru.etu.graphview.drawing.GraphEditor;
import ru.etu.graphview.drawing.ViewMode;


import java.net.URL;
import java.util.ResourceBundle;

public class App implements Initializable {
    /*
    INTERFACE OBJECTS
     */

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

    /*
    SCALING VARIABLES
     */
    /**
     * Minimum scale factor. Must be a multiple of scaleStep
     */
    private double minScale = 0.25;

    /**
     * Maximum scale factor. Must be a multiple of scaleStep
     */
    private double maxScale = 3;

    /**
     * Step of scaling
     */
    private double scaleStep = 0.25;

    private int standardWidth = 1000;
    private int standardHeight = 700;
    private int paneSizeFactor = 3;
    private boolean isJunk = true;

    private final DoubleProperty scaleFactor = new ReadOnlyDoubleWrapper(1);


    /*
    STATE VARIABLES AND CONNECTED OBJECT TO THIS STATES
     */
    private boolean isGraphCreated = false;
    public GraphPane graphPane;
    private GraphEditor graphEditor;

    private boolean isLeft = true;
    public Node top;
    public Node left;
    private ViewMode lastInstrumentType;


    @Override
    public void initialize(URL url, ResourceBundle resources) {
        /*
        GRAPH PANE INITIALISATION
         */

        graphPane = new GraphPane();
        graphPane.setPrefHeight(standardHeight * paneSizeFactor);
        graphPane.setPrefWidth(standardWidth * paneSizeFactor);

        graphPanePane.getChildren().add(graphPane);
        graphPane.toFront();

        menuBar.setViewOrder(-500);
        graphPanePane.setViewOrder(500);

        // Graph editor init
        graphEditor = new GraphEditor(graphPane);

        // Scaling init
        enablePanAndZoom();

        /*
        PANELS INITIALIZATION
         */

        top = contentBorderPane.getTop();
        left = contentBorderPane.getLeft();

        contentBorderPane.setTop(null);


        /*
        BUTTONS AND MENU INITIALIZATION
         */

        setTopButtonsDisable(true);

        settingsMenu.setDisable(true);
        saveMeMenuItem.setDisable(true);
        saveAsMenuItem.setDisable(true);

        setLeftButtonsDisable(true);

        playMenu.setDisable(true);

        //  setUpBtnsActions();
    }


    private void enablePanAndZoom() {

        graphPanePane.setOnScroll(event -> {

            String OS = System.getProperty("os.name", "generic").toLowerCase();
            if (!OS.contains("win")) {
                if (isJunk) {
                    isJunk = false;
                    return;
                } else {
                    isJunk = true;
                }
            }

            var direction = event.getDeltaY() >= 0 ? 1 : -1;

            var curScale = scaleFactor.getValue();
            var computedScale = curScale + direction * scaleStep;

            if (Double.compare(computedScale, minScale) < 0) {
                computedScale = minScale;
            } else if (Double.compare(computedScale, maxScale) > 0) {
                computedScale = maxScale;
            }

            if (curScale != computedScale) {

                graphPane.setScaleX(computedScale);
                graphPane.setScaleY(computedScale);

                if (computedScale == minScale) {
                    if (isLeft) {
                        graphPane.setTranslateX(-graphPanePane.getTranslateX() - 43 - standardWidth / 2f * (Math.max((paneSizeFactor - 1), 0)));
                        graphPane.setTranslateY(-graphPanePane.getTranslateY() - standardHeight / 2f * (Math.max((paneSizeFactor - 1), 0)));
                    } else {
                        graphPane.setTranslateX(-graphPanePane.getTranslateX() - standardWidth / 2f * (Math.max((paneSizeFactor - 1), 0)));
                        graphPane.setTranslateY(-graphPanePane.getTranslateY() - 43 - standardHeight / 2f * (Math.max((paneSizeFactor - 1), 0)));
                    }
                } else {
                    scaleFactor.setValue(computedScale);

                    // Положение в сцене
                    Bounds bounds = graphPane.localToScene(graphPane.getBoundsInLocal());
                    double f = (computedScale / curScale) - 1;
                    double dx = (event.getX() - (bounds.getWidth() / 2 + bounds.getMinX()));
                    double dy = (event.getY() - (bounds.getHeight() / 2 + bounds.getMinY()));

                    graphPane.setTranslateX(graphPane.getTranslateX() - f * dx);
                    graphPane.setTranslateY(graphPane.getTranslateY() - f * dy);
                }
            }

            event.consume();

        });

        final DragData dragData = new DragData();

        graphPanePane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                dragData.cursorType = graphPanePane.getScene().getCursor();
                graphPanePane.getScene().setCursor(Cursor.MOVE);

                dragData.mouseAnchorX = event.getX();
                dragData.mouseAnchorY = event.getY();

                dragData.translateAnchorX = graphPane.getTranslateX();
                dragData.translateAnchorY = graphPane.getTranslateY();
            }
        });

        graphPanePane.setOnMouseReleased(event -> {
            graphPanePane.getScene().setCursor(dragData.cursorType);
        });

        graphPanePane.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()) {
                graphPane.setTranslateX(dragData.translateAnchorX + event.getX() - dragData.mouseAnchorX);
                graphPane.setTranslateY(dragData.translateAnchorY + event.getY() - dragData.mouseAnchorY);
            }
        });
    }

    public void setTopButtonsDisable(boolean a) {
        prevBtn.setDisable(a);
        nextBtn.setDisable(a);
        pauseBtn.setDisable(a);
        playBtn.setDisable(a);
        stopBtn.setDisable(a);
    }

    public void setTopMenuItemsDisable(boolean a) {
        prevMenuItem.setDisable(a);
        nextMenuItem.setDisable(a);
        pauseMenuItem.setDisable(a);
        playMenuItem.setDisable(a);
        stopMenuItem.setDisable(a);
    }

    public void setLeftButtonsDisable(boolean a) {
        moveBtn.setDisable(a);
        vertexBtn.setDisable(a);
        edgeBtn.setDisable(a);
        chooseBtn.setDisable(a);
        clearBtn.setDisable(a);
    }

    public void setLeftMenuItemsDisable(boolean a) {
        moveMenuItem.setDisable(a);
        vertexMenuItem.setDisable(a);
        edgeMenuItem.setDisable(a);
        chooseMenuItem.setDisable(a);
        clearMenuItem.setDisable(a);
    }

    @FXML
    private void changeMode() {
        if (!isLeft) {
            // Setting mode

            if (lastInstrumentType != null) {
                switch (lastInstrumentType) {
                    case NORMAL -> {
                        setMoveMode();
                    }
                    case VERTEX_PLACEMENT -> {
                        setVertexCreationMode();
                    }
                    case VERTEX_CHOOSE -> {
                        setChooseMode();
                    }
                    case EDGE_PLACEMENT -> {
                        setEdgeCreationMode();
                    }
                }
            }


            if (graphEditor.getNodesSelected1() == 2) {
                graphEditor.removeAlgLabels();
            }

            // Setting buttons accessibility
            playMenu.setDisable(true);

            if (isGraphCreated) {
                setLeftButtonsDisable(false);
                setLeftMenuItemsDisable(false);
                settingsMenu.setDisable(false);
            } else {
                setLeftButtonsDisable(true);
                settingsMenu.setDisable(true);
            }

            // Translating coordinate if scale is right
            if (scaleFactor.get() == minScale + scaleStep) {
                graphPane.setTranslateX(-graphPanePane.getTranslateX() - 43 - standardWidth / 2f * (Math.max((paneSizeFactor - 1), 0)));
                graphPane.setTranslateY(-graphPanePane.getTranslateY() - standardHeight / 2f * (Math.max((paneSizeFactor - 1), 0)));
            }


            // Switching panels
            contentBorderPane.setTop(null);
            contentBorderPane.setLeft(left);
            isLeft = true;

        } else {
            // Play mode

            // Setting buttons accessibility
            settingsMenu.setDisable(true);

            if (isGraphCreated) {
                setMoveModeWithoutSaving();
            }

            if (graphPane.isGraphSet() && graphEditor.getNodesSelected1() == 2) {
                // Initialising stepEngine and preparing vertices for work
                graphEditor.addAlgLabels(graphEditor.getFirstNodeSelect().getVertex());

                playBtn.setDisable(false);

                playMenu.setDisable(false);
                setTopMenuItemsDisable(true);
                playMenuItem.setDisable(false);
            } else {
                setTopButtonsDisable(true);
                setTopMenuItemsDisable(true);
            }

            // Translating coordinate if scale is right
            if (scaleFactor.get() == minScale + scaleStep) {
                graphPane.setTranslateX(-graphPanePane.getTranslateX() - standardWidth / 2f * (Math.max((paneSizeFactor - 1), 0)));
                graphPane.setTranslateY(-graphPanePane.getTranslateY() - 43 - standardHeight / 2f * (Math.max((paneSizeFactor - 1), 0)));
            }

            // Switching panels
            contentBorderPane.setTop(top);
            contentBorderPane.setLeft(null);
            isLeft = false;
        }
    }


    // Graph creation
    @FXML
    private void createNewGraph() {
        Graph graph = new GraphList();
        var properties = new GraphViewProperties();
        properties.setNeedArrows(false);

        graphEditor.eraseGraph();
        graphPane.loadGraph(graph, properties);

        // Unblock left side tools
        setLeftButtonsDisable(false);
        settingsMenu.setDisable(false);

        // Unblock save buttons
        saveMeMenuItem.setDisable(false);
        saveAsMenuItem.setDisable(false);

        // Set move tool as active
        setMoveMode();
        moveBtn.setSelected(true);

        isGraphCreated = true;
    }

    @FXML
    private void createNewDirectedGraph() {
        Graph graph = new DirectedGraphList();
        var properties = new GraphViewProperties();
        properties.setNeedArrows(true);

        graphEditor.eraseGraph();
        graphPane.loadGraph(graph, properties);

        // Unblock left side tools
        setLeftButtonsDisable(false);
        settingsMenu.setDisable(false);

        // Unblock save buttons
        saveMeMenuItem.setDisable(false);
        saveAsMenuItem.setDisable(false);

        // Set move tool as active
        setMoveMode();
        moveBtn.setSelected(true);

        isGraphCreated = true;
    }

    /*
    GRAPH EDITING TOOLS' METHODS
     */

    private void setMoveModeWithoutSaving() {
        graphEditor.setViewMode(ViewMode.NORMAL);
    }

    @FXML
    private void setMoveMode() {
        lastInstrumentType = ViewMode.NORMAL;
        graphEditor.setViewMode(ViewMode.NORMAL);
    }

    @FXML
    private void setVertexCreationMode() {
        lastInstrumentType = ViewMode.VERTEX_PLACEMENT;
        graphEditor.setViewMode(ViewMode.VERTEX_PLACEMENT);
    }

    @FXML
    private void setEdgeCreationMode() {
        lastInstrumentType = ViewMode.EDGE_PLACEMENT;
        graphEditor.setViewMode(ViewMode.EDGE_PLACEMENT);
    }

    @FXML
    private void setChooseMode() {
        lastInstrumentType = ViewMode.VERTEX_CHOOSE;
        graphEditor.setViewMode(ViewMode.VERTEX_CHOOSE);
    }

    @FXML
    private void clearGraph() {
        graphEditor.eraseGraph();
    }

    /*
    ALGORITHM PLAY TOOLS' METHOD
     */


    @FXML
    private void play() {

    }

}

class DragData {

    Cursor cursorType;

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
}
