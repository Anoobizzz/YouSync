package yousync.ui;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yousync.service.CoreService;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
public class MenuController extends AbstractUIController {
    @FXML
    private AnchorPane menuPane;
    @FXML
    private TextField clientIdBox;
    @FXML
    private TextField clientSecretBox;
    @FXML
    private TextField playlistIdBox;
    @FXML
    private Button downloadButton;
    @FXML
    private Button menuShow;
    @Autowired
    private TableViewController tableView;
    @Autowired
    private CoreService coreService;
    @Value("${settings.youtube.id}")
    private String clientId;
    @Value("${settings.youtube.secret}")
    private String clientSecret;

    @FXML
    void initialize() {
        if (clientId != null) {
            clientIdBox.setText(clientId);
        }
        if (clientSecret != null) {
            clientSecretBox.setText(clientSecret);
        }
        initializeEventHandlers();
    }

    private void initializeEventHandlers() {
        playlistIdBox.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    final String playlistId = playlistIdBox.getText();
                    if (!isEmpty(playlistId)) {
                        coreService.checkAuthorization(playlistId, clientId, clientSecret);
                    }
                });
    }

    public void onDownloadButtonClick(final ActionEvent actionEvent) {
        coreService.downloadSongs(tableView.getSelectedSongs());
    }

    public void setMenuShowOnClickAction(final EventHandler<ActionEvent> eventHandler) {
        menuShow.setOnAction(eventHandler);
    }
}
