package ru.etu.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.etu.graph.DirectedGraphList;
import ru.etu.graph.Graph;
import ru.etu.graph.GraphList;
import ru.etu.graphview.drawing.DijkstraStepEngine;
import ru.etu.graphview.GraphPane;
import ru.etu.graphview.GraphViewProperties;
import ru.etu.graphview.drawing.GraphEditor;
import ru.etu.graphview.drawing.ViewMode;
import ru.etu.logger.Logger;

import java.io.File;
import java.io.IOException;
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
    SAVE/LOAD VARIABLES
     */
    //TODO: change type to .graph
    private final String fileType = ".json";
    //private String filePath = "default"+fileType;
    private String filePath = null;

    /*
    STATE VARIABLES AND CONNECTED OBJECT TO THIS STATES
     */
    private boolean isPlaying = false;
    private DijkstraStepEngine stepEngine;
    private int playSpeed = 3000;

    private boolean isGraphCreated = false;
    public GraphPane graphPane;
    private GraphEditor graphEditor;

    private boolean isLeft = true;
    public Node top;
    public Node left;
    private ViewMode lastInstrumentType;

    private boolean isLoggerOpen = false;
    private Stage loggerStage;
    private Logger loggerInstance;

    private boolean isSettingsOpen = false;
    private Stage settingsStage;

    private boolean isAboutOpen = false;
    private Stage aboutStage;

    Settings settingsController = null;

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
        SETTINGS INITIALIZATION
         */
        try {
            FXMLLoader settingsFxmlLoader = new FXMLLoader(getClass().getResource("/ru/etu/studypract/settings.fxml"));

            Scene appScene = new Scene(settingsFxmlLoader.load());
            settingsStage = new Stage();
            settingsStage.setTitle("Logger");
            settingsStage.setScene(appScene);

            settingsStage.setOnCloseRequest((event) -> {
                settingsStage.hide();
                isSettingsOpen = false;
            });

            settingsController = settingsFxmlLoader.getController();
            settingsController.setApp(this);
            settingsController.setData(playSpeed, maxScale, minScale, scaleStep, paneSizeFactor);
        } catch (IOException ioException) {
            System.err.println("Failed to load settings fxml!");
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        /*
        LOGGER INITIALIZATION
         */

        LoggerView loggerViewController = null;
        try {
            FXMLLoader loggerFxmlLoader = new FXMLLoader(getClass().getResource("/ru/etu/studypract/loggerView.fxml"));

            Scene appScene = new Scene(loggerFxmlLoader.load());
            loggerStage = new Stage();
            loggerStage.setTitle("Logger");
            loggerStage.setScene(appScene);
            loggerStage.setMinWidth(600);
            loggerStage.setMinHeight(400);

            loggerStage.setOnCloseRequest((event) -> {
                loggerStage.hide();
                isLoggerOpen = false;
            });

            loggerViewController = loggerFxmlLoader.getController();
        } catch (IOException ioException) {
            System.err.println("Failed to load logger fxml!");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Logger.initialiseInstance(loggerViewController);
        loggerInstance = Logger.getInstance();

        /*
        BUTTONS AND MENU INITIALIZATION
         */

        setTopButtonsDisable(true);

        settingsMenu.setDisable(true);
        saveMeMenuItem.setDisable(true);
        saveAsMenuItem.setDisable(true);

        setLeftButtonsDisable(true);

        playMenu.setDisable(true);

        setUpBtnsActions();
    }

    private void setUpBtnsActions() {
        //buttons/menu items
        exitMenuItem.setOnAction(event -> eventCloseWindow());

        changeMenuItem.setOnAction(event -> {
            changeMode();
        });

        EventHandler<ActionEvent> vertexBtnSelectHandler = event -> {
            setVertexCreationMode();
            if (!vertexBtn.isSelected()) {
                vertexBtn.setSelected(true);
            }
        };
        EventHandler<ActionEvent> edgeBtnSelectHandler = event -> {
            setEdgeCreationMode();
            if (!edgeBtn.isSelected()) {
                edgeBtn.setSelected(true);
            }
        };
        EventHandler<ActionEvent> moveBtnSelectHandler = event -> {
            setMoveMode();
            if (!moveBtn.isSelected()) {
                moveBtn.setSelected(true);
            }
        };
        EventHandler<ActionEvent> chooseBtnSelectHandler = event -> {
            setChooseMode();
            if (!chooseBtn.isSelected()) {
                chooseBtn.setSelected(true);
            }
        };
        EventHandler<ActionEvent> clearBtnSelectHandler = event -> {
            clearGraph();
        };
        EventHandler<ActionEvent> playBtnSelectHandler = event -> {
            play();
            if (!playBtn.isSelected()) {
                playBtn.setSelected(true);
            }
        };
        EventHandler<ActionEvent> stopBtnSelectHandler = event -> {
            stop();
            if (playBtn.isSelected()) {
                playBtn.setSelected(false);
            }
            if (pauseBtn.isSelected()) {
                pauseBtn.setSelected(false);
            }
        };
        EventHandler<ActionEvent> nextBtnSelectHandler = event -> {
            stepForward();
            pauseBtn.setSelected(true);
        };
        EventHandler<ActionEvent> prevBtnSelectHandler = event -> {
            stepBack();
            pauseBtn.setSelected(true);
        };
        EventHandler<ActionEvent> pauseBtnSelectHandler = event -> {
            pause();
            if (!pauseBtn.isSelected()) {
                pauseBtn.setSelected(true);
            }
        };

        vertexBtn.setOnAction(vertexBtnSelectHandler);
        edgeBtn.setOnAction(edgeBtnSelectHandler);
        moveBtn.setOnAction(moveBtnSelectHandler);
        chooseBtn.setOnAction(chooseBtnSelectHandler);
        clearBtn.setOnAction(clearBtnSelectHandler);
        playBtn.setOnAction(playBtnSelectHandler);
        stopBtn.setOnAction(stopBtnSelectHandler);
        nextBtn.setOnAction(nextBtnSelectHandler);
        prevBtn.setOnAction(prevBtnSelectHandler);
        pauseBtn.setOnAction(pauseBtnSelectHandler);

        vertexMenuItem.setOnAction(vertexBtnSelectHandler);
        edgeMenuItem.setOnAction(edgeBtnSelectHandler);
        moveMenuItem.setOnAction(moveBtnSelectHandler);
        chooseMenuItem.setOnAction(chooseBtnSelectHandler);
        clearMenuItem.setOnAction(clearBtnSelectHandler);
        playMenuItem.setOnAction(playBtnSelectHandler);
        stopMenuItem.setOnAction(stopBtnSelectHandler);
        nextMenuItem.setOnAction(nextBtnSelectHandler);
        prevMenuItem.setOnAction(prevBtnSelectHandler);
        pauseMenuItem.setOnAction(pauseBtnSelectHandler);
    }

    private void enablePanAndZoom() {

        graphPanePane.setOnScroll(event -> {

            String OS = System.getProperty("os.name", "generic").toLowerCase();
            //System.out.println(OS);
            if(!OS.contains("win")){
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

            // Stopping threads in stepEngine if necessary
            if (isPlaying) {
                stop();
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

            // Push log message
            loggerInstance.printMessage(getClass().getName(), "Switching to Setting mode.");

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
                stepEngine = new DijkstraStepEngine(graphEditor, playSpeed);

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

            // Push log message
            loggerInstance.printMessage(getClass().getName(), "Switching to play mode.");

            // Switching panels
            contentBorderPane.setTop(top);
            contentBorderPane.setLeft(null);
            isLeft = false;
        }
    }

    public void eventCloseWindow() {
        if (isGraphCreated && isPlaying) {
            stepEngine.stop();
        }

        Platform.exit();
    }

    public void closeWindow(Stage stage) {
        stage.setOnCloseRequest(event -> eventCloseWindow());
    }



    @FXML
    private void openLogger() {
        if (!isLoggerOpen) {
            isLoggerOpen = true;
            loggerStage.show();
        } else {
            loggerStage.requestFocus();
        }
    }

    @FXML
    private void openSettings() throws IOException {
        if (!isSettingsOpen || settingsController.getClosed()) {
            isSettingsOpen = true;
            settingsController.setClosed(false);
            settingsStage.show();
        } else {
            settingsStage.requestFocus();
        }
    }

    public void setSettings(int playSpeed, double zoomStep, double zoomMax, double zoomMin, int typeOfSize) {

        this.playSpeed = playSpeed;
        scaleStep = zoomStep;
        maxScale = zoomMax;
        minScale = zoomMin;
        paneSizeFactor = typeOfSize;

        if (!isLeft) {
            changeMode();
        }

        graphPane.setPrefHeight(standardHeight * paneSizeFactor);
        graphPane.setPrefWidth(standardWidth * paneSizeFactor);

        scaleFactor.setValue(minScale);

        graphPane.setScaleX(minScale);
        graphPane.setScaleY(minScale);

        graphPane.setTranslateX(-graphPanePane.getTranslateX() - 43 - standardWidth / 2f * (Math.max((paneSizeFactor - 1), 0)));
        graphPane.setTranslateY(-graphPanePane.getTranslateY() - standardHeight / 2f * (Math.max((paneSizeFactor - 1), 0)));


        enablePanAndZoom();
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
        filePath=null;
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
        filePath=null;
    }

    /*
    GRAPH EDITING TOOLS' METHODS
     */

    private void setMoveModeWithoutSaving() {
        graphEditor.setViewMode(ViewMode.NORMAL);
        loggerInstance.printMessage(getClass().getName(), "Move mode activated. You can now move any vertex you want.");
    }

    @FXML
    private void setMoveMode() {
        lastInstrumentType = ViewMode.NORMAL;
        graphEditor.setViewMode(ViewMode.NORMAL);
        loggerInstance.printMessage(getClass().getName(), "Move mode activated. You can now move any vertex you want.");
    }

    @FXML
    private void setVertexCreationMode() {
        lastInstrumentType = ViewMode.VERTEX_PLACEMENT;
        graphEditor.setViewMode(ViewMode.VERTEX_PLACEMENT);
        loggerInstance.printMessage(getClass().getName(), "Vertex creation mode activated. You can now place new vertices on the screen (Moving vertices is allowed). Just click on an empty space of the pane.");
    }

    @FXML
    private void setEdgeCreationMode() {
        lastInstrumentType = ViewMode.EDGE_PLACEMENT;
        graphEditor.setViewMode(ViewMode.EDGE_PLACEMENT);
        loggerInstance.printMessage(getClass().getName(), "Edge creation mode activated. You can now place new edges on the screen (Moving vertices is forbidden). Choose two vertices to create edge between them.");
    }

    @FXML
    private void setChooseMode() {
        lastInstrumentType = ViewMode.VERTEX_CHOOSE;
        graphEditor.setViewMode(ViewMode.VERTEX_CHOOSE);
        loggerInstance.printMessage(getClass().getName(), "Vertex choose mode activated. You can now choose two vertices to make Dijkstra algorithm find the shortest bath between them.");
    }

    @FXML
    private void clearGraph() {
        graphEditor.eraseGraph();
        loggerInstance.printMessage(getClass().getName(), "You just cleared the graph. Now you can place new elements.");
    }

    /*
    ALGORITHM PLAY TOOLS' METHOD
     */
    @FXML
    private void stepBack() {
        stepEngine.makeStepBackwards();
    }

    @FXML
    private void stepForward() {
        stepEngine.makeStepForward();
    }

    @FXML
    private void play() {
        setMoveMode();
        if (!stepEngine.isInitialised()) {
            stepEngine.applyDijkstra();
            if (stepEngine.isPathExists()) {
                stepEngine.startAutoPlay();
                isPlaying = true;
            }

        } else if (stepEngine.isPaused()) {
            stepEngine.resumeAutoPlay();
            isPlaying = true;
        } else if (!isPlaying) {
            stepEngine.startAutoPlay();
            isPlaying = true;
        }

        if (stepEngine.isPathExists()) {
            setTopButtonsDisable(false);
            setTopMenuItemsDisable(false);
            playMenu.setDisable(false);
        }
    }

    @FXML
    private void pause() {
        stepEngine.pauseAutoPlay();
    }

    @FXML
    private void stop() {
        stepEngine.stop();

        setTopButtonsDisable(true);
        playBtn.setDisable(false);

        setTopMenuItemsDisable(true);
        playMenuItem.setDisable(false);

        pauseBtn.setSelected(false);
        playBtn.setSelected(false);

        isPlaying = false;
    }
    
    private String fixFileName(File path){
        String pathStr = path.toString();
        String OS = System.getProperty("os.name", "generic").toLowerCase();
        if(!pathStr.endsWith(fileType)&&!OS.contains("win")){
            return pathStr+fileType;
        }
        return pathStr;
    }
}

class DragData {

    Cursor cursorType;

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
}
