package ru.etu.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;

public class Settings {

    public ToggleGroup fieldSizeToggleGroup;

    private int playSpeed = 500;
    private double zoomStep = 0.25;
    private double zoomMax = 10;
    private double zoomMin = 0.5;
    private int typeOfSize = 2;

    private int playSpeedTmp;
    private double zoomStepTmp;
    private double zoomMaxTmp;
    private double zoomMinTmp;
    private int typeOfSizeTmp;

    @FXML
    private Button cancelBtn;
    @FXML
    private Button applyBtn;

    @FXML
    private TextField maxZoomTextField;

    @FXML
    private TextField minZoomTextField;

    @FXML
    private Button okBtn;
    @FXML
    private Button increaseBtn;
    @FXML
    private Button decreaseBtn;

    @FXML
    private RadioButton smallBtn;
    @FXML
    private RadioButton normalBtn;
    @FXML
    private RadioButton hugeBtn;

    @FXML
    private TextField playSpeedTextField;

    @FXML
    private TextField zoomStepTextField;

    private boolean isClosed = false;

    private App myApp;

    private boolean isPlaySpeedOK = true;
    private boolean isMaxZoomOK = true;
    private boolean isMinZoomOK = true;

    @FXML
    void initialize() {
        smallBtn.setUserData(1);
        normalBtn.setUserData(2);
        hugeBtn.setUserData(3);

        applyBtn.setOnAction(event -> {

            playSpeed = playSpeedTmp;
            zoomStep = zoomStepTmp;
            zoomMax = zoomMaxTmp;
            zoomMin = zoomMinTmp;
            typeOfSize = typeOfSizeTmp;

            myApp.setSettings(playSpeed, zoomStep, zoomMax, zoomMin, typeOfSize);
//            setAllTexts();
            applyBtn.setDisable(true);
        });

        okBtn.setOnAction(event -> {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            isClosed = true;

            if (!(playSpeed == playSpeedTmp && zoomMax == zoomMaxTmp && zoomMin == zoomMinTmp && zoomStep == zoomStepTmp && typeOfSize == typeOfSizeTmp)) {
                playSpeed = playSpeedTmp;
                zoomStep = zoomStepTmp;
                zoomMax = zoomMaxTmp;
                zoomMin = zoomMinTmp;
                typeOfSize = typeOfSizeTmp;

                myApp.setSettings(playSpeed, zoomStep, zoomMax, zoomMin, typeOfSize);
                applyBtn.setDisable(true);
            }


        });

        cancelBtn.setOnAction(event -> {
            ((Node) (event.getSource())).getScene().getWindow().hide();
            isClosed = true;

            playSpeedTmp = playSpeed;
            zoomStepTmp = zoomStep;
            zoomMaxTmp = zoomMax;
            zoomMinTmp = zoomMin;
            typeOfSizeTmp = typeOfSize;

            setAllTexts();

            playSpeedTextField.setStyle("-fx-border-color: green;");
            zoomStepTextField.setStyle("-fx-border-color: green;");
            minZoomTextField.setStyle("-fx-border-color: green;");
            maxZoomTextField.setStyle("-fx-border-color: green;");
        });

        increaseBtn.setOnAction(event -> {
            if (zoomStepTmp < 0.5) {
                zoomStepTmp = (zoomStepTmp * 100 + 5) / 100;
            }
            zoomStepTextField.setText(String.valueOf(zoomStepTmp));
            validateMinFieldData();
            validateMaxFieldData();

            checkSettings();
        });

        decreaseBtn.setOnAction(event -> {
            if (zoomStepTmp > 0.05) {
                zoomStepTmp = (zoomStepTmp * 100 - 5) / 100;
            }
            zoomStepTextField.setText(String.valueOf(zoomStepTmp));
            validateMinFieldData();
            validateMaxFieldData();

            checkSettings();
        });

        fieldSizeToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            typeOfSizeTmp = (int) newValue.getUserData();

            checkSettings();
        }));

        playSpeedTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {
                        playSpeedTextField.setText(newValue.replaceAll("\\D", ""));
                    }

                    validatePlaySpeed();
                    if (isPlaySpeedOK) {
                        playSpeedTmp = Integer.parseInt(playSpeedTextField.getText());
                    }
                });

        maxZoomTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*\\.\\d*")) {
                        maxZoomTextField.setText(newValue.replaceAll("[^\\d.]", ""));
                    }

                    validateMaxFieldData();
                    if (isMaxZoomOK) {
                        zoomMaxTmp = Double.parseDouble(maxZoomTextField.getText());
                    }
                    checkSettings();
                });

        minZoomTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*\\.\\d*")) {
                        minZoomTextField.setText(newValue.replaceAll("[^\\d.]", ""));
                    }

                    validateMinFieldData();
                    if (isMinZoomOK) {
                        zoomMinTmp = Double.parseDouble(minZoomTextField.getText());
                    }
                    checkSettings();
                });


    }

    private void validatePlaySpeed() {
        if (playSpeedTextField.getText().length() == 0) {
            playSpeedTextField.setStyle("-fx-border-color: red;");
            isPlaySpeedOK = false;
            checkSettings();
        } else {
            try {
                int input = Integer.parseInt(playSpeedTextField.getText());
                if (input <= 0) {
                    playSpeedTextField.setStyle("-fx-border-color: red;");
                    isPlaySpeedOK = false;
                    checkSettings();
                } else {
                    playSpeedTextField.setStyle("-fx-border-color: green;");
                    isPlaySpeedOK = true;
                    checkSettings();
                }

            } catch (Exception e) {
                playSpeedTextField.setStyle("-fx-border-color: red;");
                isPlaySpeedOK = false;
                checkSettings();
            }
        }

    }

    private void validateMaxFieldData() {
        if (maxZoomTextField.getText().length() == 0) {
            maxZoomTextField.setStyle("-fx-border-color: red;");
            isMaxZoomOK = false;
            checkSettings();
        } else {
            try {
                double input = Double.parseDouble(maxZoomTextField.getText());
                if (input > 10 || input < zoomStepTmp || ((int) (input * 100) % (int) (zoomStepTmp * 100)) != 0) {
                    maxZoomTextField.setStyle("-fx-border-color: red;");
                    isMaxZoomOK = false;
                    checkSettings();
                } else {
                    maxZoomTextField.setStyle("-fx-border-color: green;");
                    isMaxZoomOK = true;
                    checkSettings();
                }
            } catch (NumberFormatException e) {
                maxZoomTextField.setStyle("-fx-border-color: red;");
                isMaxZoomOK = false;
                checkSettings();
            }
        }
    }

    private void validateMinFieldData() {
        if (minZoomTextField.getText().length() == 0) {
            minZoomTextField.setStyle("-fx-border-color: red;");
            isMinZoomOK = false;
            checkSettings();
        } else {
            try {
                double input = Double.parseDouble(minZoomTextField.getText());
                if (input > 10 || input < zoomStepTmp || ((int) (input * 100) % (int) (zoomStepTmp * 100)) != 0) {
                    minZoomTextField.setStyle("-fx-border-color: red;");
                    isMinZoomOK = false;
                    checkSettings();
                } else {
                    minZoomTextField.setStyle("-fx-border-color: green;");
                    isMinZoomOK = true;
                    checkSettings();
                }
            } catch (NumberFormatException e) {
                minZoomTextField.setStyle("-fx-border-color: red;");
                isMinZoomOK = false;
                checkSettings();
            }
        }
    }

    private void setAllTexts() {
        playSpeedTextField.setText(String.valueOf(playSpeed));
        zoomStepTextField.setText(String.valueOf(zoomStep));
        minZoomTextField.setText(String.valueOf(zoomMin));
        maxZoomTextField.setText(String.valueOf(zoomMax));
    }

    private void checkSettings() {
        if (isPlaySpeedOK && isMaxZoomOK && isMinZoomOK) {
            if (playSpeed == playSpeedTmp && zoomMax == zoomMaxTmp && zoomMin == zoomMinTmp && zoomStep == zoomStepTmp && typeOfSize == typeOfSizeTmp) {
                okBtn.setDisable(false);
                applyBtn.setDisable(true);
            } else {
                okBtn.setDisable(false);
                applyBtn.setDisable(false);
            }
        } else {
            okBtn.setDisable(true);
            applyBtn.setDisable(true);
        }
    }

    public void setApp(App app) {
        myApp = app;
    }

    public void setData(int playSpeed, double zoomMax, double zoomMin, double zoomStep, int typeOfSize) {
        this.playSpeed = playSpeed;
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
        this.zoomStep = zoomStep;
        this.typeOfSize = typeOfSize;

        playSpeedTmp = this.playSpeed;
        zoomStepTmp = this.zoomStep;
        zoomMaxTmp = this.zoomMax;
        zoomMinTmp = this.zoomMin;
        typeOfSizeTmp = this.typeOfSize;

        isClosed = true;
        setAllTexts();
        applyBtn.setDisable(true);

        playSpeedTextField.setStyle("-fx-border-color: green;");
        zoomStepTextField.setStyle("-fx-border-color: green;");
        minZoomTextField.setStyle("-fx-border-color: green;");
        maxZoomTextField.setStyle("-fx-border-color: green;");

        switch (this.typeOfSize) {
            case 1 -> fieldSizeToggleGroup.selectToggle(smallBtn);
            case 2 -> fieldSizeToggleGroup.selectToggle(normalBtn);
            case 3 -> fieldSizeToggleGroup.selectToggle(hugeBtn);
        }
    }

    public boolean getClosed() {
        return isClosed;
    }

    public void setClosed(boolean b) {
        isClosed = b;
    }
}

