package yousync.ui;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import yousync.domain.Song;
import yousync.service.CoreService;
import yousync.sources.YouTubeSource;

import java.io.File;
import java.io.IOException;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;

@Component
public class MainController extends AbstractUIController {
    private static Stage authStage;

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
    private TabPane parentTabPane;

    @Autowired
    private CoreService coreService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private YouTubeSource youTubeSource;

    @Value("${settings.youtube.id:#{null}}")
    private String clientId;
    @Value("${settings.youtube.secret:#{null}}")
    private String clientSecret;

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
        runLater(() -> authStage = new Stage());
    }

    public void loadContent(ObservableList<Song> songs) {
        loadSongs(songs);
    }

    private void loadLocalSongs() {
        File[] localFiles = directoryResolver.getDownloadDirectory()
                .listFiles((dir, name) -> name.matches("^.*\\.mp3+$"));
        if (localFiles != null) {
            ObservableList<Song> songs = observableArrayList();
            try {
                for (File file : localFiles) {
                    Mp3File mp3File = new Mp3File(file);
                    ID3Wrapper wrapper = new ID3Wrapper(mp3File.getId3v1Tag(), mp3File.getId3v2Tag());
                    songs.add(new Song(wrapper.getArtist(), wrapper.getTitle(), wrapper.getAlbumImage()));
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                //TODO: Error handling
                e.printStackTrace();
            }
            storeContent(songs);
            loadNewContent(songs);
        }
    }

    public static void showErrorWindow(String errorMessage) {
        //TODO: Proper error handling?
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void initializeEventHandlers() {
        loadButton.setOnAction(event -> coreService.checkAuthorization(youTubeSource, playlistIdBox.getText()));
        downloadButton.setOnAction(event -> coreService.downloadSongs(getSelectedSongs()));
    }

    public void displayWebAuthenticationWindow(String page) {
        WebView webView = new WebView();
        webView.getEngine().loadContent(page);
        Scene scene = new Scene(webView);
        authStage.setScene(scene);
        authStage.show();
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

