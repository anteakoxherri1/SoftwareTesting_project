package com.electronicstore.view.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class AlertDialog {
    private final Alert alert;

    public AlertDialog(Alert.AlertType type, String title, String header, String content) {
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
    }

    public void setIcon(Stage ownerStage) {
        if (ownerStage != null) {
            alert.initOwner(ownerStage);
        }
    }

    public ButtonType showAndWait() {
        return alert.showAndWait().orElse(ButtonType.CANCEL);
    }

    public static void showInfo(String title, String content) {
        new AlertDialog(Alert.AlertType.INFORMATION, title, null, content).showAndWait();
    }

    public static void showError(String title, String content) {
        new AlertDialog(Alert.AlertType.ERROR, title, null, content).showAndWait();
    }

    public static void showWarning(String title, String content) {
        new AlertDialog(Alert.AlertType.WARNING, title, null, content).showAndWait();
    }

    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}