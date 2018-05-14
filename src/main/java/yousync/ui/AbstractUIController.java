package yousync.ui;

import javafx.collections.ObservableList;
import yousync.domain.Song;

public abstract class AbstractUIController {
    public void loadSongs(ObservableList<Song> songs) {
        storeContent(songs);
        loadNewContent(songs);
    }

    abstract void storeContent(ObservableList<Song> items);

    abstract void loadNewContent(ObservableList<Song> nodes);
}
