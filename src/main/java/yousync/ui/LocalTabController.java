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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yousync.domain.Song;

import java.io.File;
import java.io.IOException;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableArrayList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static yousync.services.CoreService.DEFAULT_DEST;

@Component(value = "localTab")
public class LocalTabController {
    private static ObservableList<Song> songs = observableArrayList();

    @Value("${settings.music.directory:#{null}}")
    private String musicDirectoryDest;

    @Autowired
    private MainController mainController;

    @FXML
    private Tab localTab;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TableView tableView;

    @FXML
    void initialize() {
        if (isEmpty(musicDirectoryDest)) {
            musicDirectoryDest = DEFAULT_DEST;
        }
        loadSongs();
    }

    private void loadSongs() {
        File musicDirectory = new File(musicDirectoryDest);
        if (!musicDirectory.exists()) {
            if (musicDirectory.mkdir()) {
                runLater(() -> mainController.showErrorWindow("Failed to create directory for music"));
            }
        }
        File[] localFiles = musicDirectory.listFiles((dir, name) -> name.matches("^.*\\.mp3+$"));
        ObservableList<Song> songs = observableArrayList();
        try {
            for (File file : localFiles) {
                Mp3File mp3File = new Mp3File(file);
                ID3Wrapper wrapper = new ID3Wrapper(mp3File.getId3v1Tag(), mp3File.getId3v2Tag());
                songs.add(new Song(wrapper.getArtist(), wrapper.getTitle(), wrapper.getAlbumImage()));
            }
        } catch (IOException e) {
            //TODO: Error handling
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        storeContent(songs);
        loadNewContent(songs);
    }

    private void storeContent(ObservableList<Song> songs) {
        LocalTabController.songs.addAll(songs);
    }

    private void loadNewContent(ObservableList<Song> songs) {
        runLater(() -> tableView.setItems(observableArrayList(songs)));
    }
}
