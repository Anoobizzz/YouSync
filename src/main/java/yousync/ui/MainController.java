package yousync.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import yousync.domain.Song;
import yousync.service.CoreService;

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

    public void loadContent(ObservableList<Song> songs){
        appContext.getBean(getActiveTab().getId(), AbstractUIController.class).loadSongs(songs);
    }

    public static void showErrorWindow(String errorMessage) {
        //TODO: Proper error handling?
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}

