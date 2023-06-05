package com.ngoclam.querytool.util;

import javafx.scene.control.Alert;

public class CustomAlert {
    public static Alert error(String windowTitle, String header, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(description);
        return alert;
    }

    public static Alert infor(String windowTitle, String header, String description) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(windowTitle);
        alert.setHeaderText(header);
        alert.setContentText(description);
        return alert;
    }
}