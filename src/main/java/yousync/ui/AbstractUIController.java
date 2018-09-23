package yousync.ui;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import yousync.domain.Song;
import yousync.service.DirectoryResolverService;

import static javafx.collections.FXCollections.observableArrayList;

public abstract class AbstractUIController {
    @Autowired
    protected DirectoryResolverService directoryResolver;

    protected ObservableList<Song> songs = observableArrayList();

    protected void loadSongs(ObservableList<Song> songs) {
        storeContent(songs);
        loadNewContent(songs);
    }

    protected ObservableList<Song> getSelectedSongs() {
        return songs.filtered(Song::isSelected);
    }

    protected void storeContent(ObservableList<Song> items) {
        songs.addAll(items);
    }

    abstract void loadNewContent(ObservableList<Song> nodes);
}