package ru.etu.controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class LoggerView implements Initializable {

    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textArea.textProperty().addListener((ObservableValue<?> observable, Object oldValue,
                                             Object newValue) -> {
            textArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void printMessage(String className, String message) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        textArea.appendText(formatter.format(date) + "\t[" + className + "]: " + message + "\n");
    }
}
