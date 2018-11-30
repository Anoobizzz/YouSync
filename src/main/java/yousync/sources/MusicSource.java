package yousync.sources;

import yousync.domain.PlaylistRequest;
import yousync.domain.PlaylistResponse;

import java.util.concurrent.CompletableFuture;

public interface MusicSource {
    default PlaylistResponse getPlaylist(PlaylistRequest playlistRequest) {
        throw new UnsupportedOperationException("This music source does not support this flow!");
    }

    CompletableFuture<Void> authorize();

    boolean isAuthorized();

    default boolean requiresAuthorization(String playlist) {
        return false;
    }
}
