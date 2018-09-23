package yousync.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yousync.domain.Song;
import yousync.service.CoreService;
import yousync.sources.YouTubeSource;

import static javafx.application.Platform.runLater;

@Component(value = "youTubeTab")
public class YouTubeTabController extends AbstractUIController {
    private static Stage authStage;

    @Value("${settings.youtube.id:#{null}}")
    private String clientId;
    @Value("${settings.youtube.secret:#{null}}")
    private String clientSecret;

    @Autowired
    private CoreService coreService;
    @Autowired
    private YouTubeSource youTubeSource;

    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private TableView<Song> tableView;
    @FXML
    private Button downloadButton;
    @FXML
    private Button loadButton;
    @FXML
    private TextField playlistIdBox;
    @FXML
    private TextField clientSecretBox;
    @FXML
    private TextField clientIdBox;

    @FXML
    void initialize() {
        leftPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.22));
        initializeEventHandlers();
        if (clientId != null) {
            clientIdBox.setText(clientId);
        }
        if (clientSecret != null) {
            clientSecretBox.setText(clientSecret);
        }
    }

    private void initializeEventHandlers() {
        loadButton.setOnAction(event -> coreService.checkAuthorization(youTubeSource, playlistIdBox.getText()));
        downloadButton.setOnAction(event -> coreService.downloadSongs(getSelectedSongs()));
    }

    public void displayWebAuthenticationWindow(String page) {
        Stage authStage = new Stage();
        WebView webView = new WebView();
        webView.getEngine().loadContent(page);
        Scene scene = new Scene(webView);
        authStage.setScene(scene);
        authStage.show();
        YouTubeTabController.authStage = authStage;
    }

    public void closeWebAuthenticationWindow() {
        runLater(() -> authStage.hide());
    }

    public String getPlaylistIdBoxText() {
        return playlistIdBox.getText();
    }

    public String getClientSecretBoxText() {
        return clientSecretBox.getText();
    }

    public String getClientIdBoxText() {
        return clientIdBox.getText();
    }

    @Override
    void loadNewContent(ObservableList<Song> nodes) {
        runLater(() -> tableView.setItems(songs));
    }
}