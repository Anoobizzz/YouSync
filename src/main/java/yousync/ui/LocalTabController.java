package yousync.ui;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yousync.domain.Song;

import java.io.File;
import java.io.IOException;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;

@Component(value = "localTab")
public class LocalTabController extends AbstractUIController {
    @Autowired
    private MainController mainController;

    @FXML
    private Tab localTab;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TableView<Song> tableView;

    @FXML
    void initialize() {
        loadSongs();
    }

    private void loadSongs() {
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

    protected void loadNewContent(ObservableList<Song> songs) {
        runLater(() -> tableView.setItems(observableArrayList(songs)));
    }
}
