package ru.etu.studypract;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader appFxmlLoader = new FXMLLoader(getClass().getResource("/ru/etu/studypract/app.fxml"));

        Scene appScene = new Scene(appFxmlLoader.load());
        stage.setTitle("Dijkstra Visualiser");
        stage.setScene(appScene);
        stage.show();


        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}