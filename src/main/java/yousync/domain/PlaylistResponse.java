package yousync.domain;

import javafx.collections.ObservableList;

import java.util.List;

import static javafx.collections.FXCollections.observableList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PlaylistResponse {
    private ObservableList<Song> currentSongs;
    private String nextPageToken;

    public PlaylistResponse(List<Song> currentSongs, String nextPageToken) {
        this.currentSongs = observableList(currentSongs);
        this.nextPageToken = nextPageToken;
    }

    public ObservableList<Song> getCurrentSongs() {
        return currentSongs;
    }

    public boolean hasNext() {
        return isNotEmpty(nextPageToken);
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
