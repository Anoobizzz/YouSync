package yousync.ui;

import com.mpatric.mp3agic.ID3Wrapper;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.media.Media;
import org.springframework.stereotype.Component;
import yousync.domain.Song;

import java.io.File;
import java.io.IOException;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;

@Component
public class TableViewController extends AbstractUIController {
    private ObservableList<Song> songs = observableArrayList();
    @FXML
    private TableView<Song> tableView;
    @FXML
    private CheckBox selectAll;
    @FXML
    private TableColumn image;
    @FXML
    private TableColumn artist;
    @FXML
    private TableColumn title;
    @FXML
    private TableColumn selected;

    @FXML
    void initialize() {
        image.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        artist.maxWidthProperty().bind(tableView.widthProperty().multiply(0.325));
        title.maxWidthProperty().bind(tableView.widthProperty().multiply(0.325));
        selected.maxWidthProperty().bind(tableView.widthProperty().multiply(0.05));
    }

    private void loadSongs(ObservableList<Song> songs) {
        storeContent(songs);
        loadNewContent(songs);
    }

    protected ObservableList<Song> getSelectedSongs() {
        return songs.filtered(Song::isSelected);
    }

    private void storeContent(ObservableList<Song> items) {
        songs.addAll(items);
    }

    public void loadContent(ObservableList<Song> songs) {
        loadSongs(songs);
    }

    private void loadLocalSongs() {
        File[] localFiles = configurationService.getDownloadDirectory()
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

    private void loadNewContent(ObservableList<Song> nodes) {
        runLater(() -> tableView.setItems(songs));
    }

    public Media getSelectedSong() {
        return null;
    }
}
