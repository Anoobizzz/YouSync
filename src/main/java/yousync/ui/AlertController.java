package yousync.ui;

import javafx.scene.control.Alert;

public class AlertController {
    public static void showErrorWindow(String errorMessage) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
