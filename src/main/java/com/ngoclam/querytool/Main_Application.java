package com.ngoclam.querytool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class Main_Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main_Application.class.getResource("/com/ngoclam/querytool/ui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Main_Application.class.getResource("/com/ngoclam/querytool/ui.css").toExternalForm());
        stage.setTitle("Query Tool");
        stage.setScene(scene);
        String icon = this.getClass().getResource("/icons/icon.png").toExternalForm();
        stage.getIcons().add(new Image(icon));
        try {
            final Task<Integer> task = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    Thread.sleep(2000);
                    Platform.runLater(new Runnable() {
                        public void run() {
                            showStage(stage);
                        }
                    });
                    return 0;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    public void showStage(Stage stage) {
        stage.show();
        stage.setOnCloseRequest(we -> {
            Alert alert =
                    new Alert(Alert.AlertType.CONFIRMATION,
                            "Chắc chưa bạn?",
                            ButtonType.OK,
                            ButtonType.CANCEL);
            alert.setHeaderText("Lưu câu lệnh và xuất file excel nếu cần");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
            }
        });
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
    }

    public static void main(String[] args) {
        launch();
    }
}