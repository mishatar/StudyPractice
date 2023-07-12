package ru.etu.logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.etu.controllers.LoggerView;

public class Logger {

    private static Logger instance;

    private LoggerView loggerView;

    private Logger() {}
    private Logger(LoggerView view) {
        loggerView = view;
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    public static void initialiseInstance(LoggerView view) {
        if (view == null)
            throw new IllegalArgumentException("View can't be null.");
        if (instance == null) {
            instance = new Logger(view);
        } else if (instance.loggerView == null) {
            instance.loggerView = view;
        }
    }

    public void printMessage(String className, String message) {
        loggerView.printMessage(className, message);
    }

    public void printMessage(String className, String message, boolean isError){
        if(isError){
            Alert err = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            err.showAndWait();
        }
        printMessage(className, message);
    }

    public void printMessage(String className, String message, boolean isError, Alert.AlertType type){
        Platform.runLater(() -> {
            if(isError){
                Alert err = new Alert(type, message, ButtonType.OK);
                err.show();
//            err.showAndWait();
            }
            printMessage(className, message);
        });
    }
}
