package org.example.toy_social_v1_1.factory;

import javafx.scene.control.Alert;

public class AlertFactory {
    private static AlertFactory instance;

    private AlertFactory() {}

    public static AlertFactory getInstance() {
        if (instance == null) {
            instance = new AlertFactory();
        }
        return instance;
    }

    public Alert createErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    public Alert createExceptionAlert(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(e.getMessage());
        return alert;
    }

    public Alert createNotificationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    public Alert createAlreadySentRequestAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }
}
