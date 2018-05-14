package yousync.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import yousync.services.CoreService;

@Component
public class MainController {
    @Autowired
    private CoreService coreService;
    @Autowired
    private ApplicationContext appContext;

    @FXML
    private TabPane parentTabPane;

    private Tab getActiveTab() {
        return parentTabPane.getSelectionModel().getSelectedItem();
    }

    public AbstractUIController resolveActiveUIController() {
        return appContext.getBean(getActiveTab().getId(), AbstractUIController.class);
    }

    void showErrorWindow(String errorMessage) {
        //TODO: Proper error handling?
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}

