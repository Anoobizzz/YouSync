package yousync.sources;

import yousync.domain.PlaylistRequest;
import yousync.domain.PlaylistResponse;

import java.net.MalformedURLException;

public interface MusicSource {
    default PlaylistResponse getPlaylist(PlaylistRequest playlistRequest) throws MalformedURLException {
        throw new UnsupportedOperationException("This music source does not support this flow!");
    }

    void authorize();

    boolean isAuthorized();
}
