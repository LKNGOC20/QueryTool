package com.ngoclam.querytool;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen extends Preloader {

    Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        StackPane root = (StackPane) FXMLLoader.load(SplashScreen.class.getResource("splash.fxml"));
        Scene scene = new Scene(root, 500, 280, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof StateChangeNotification) {
            //hide after get any state update from application
            stage.hide();
        }
    }
}