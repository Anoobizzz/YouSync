package yousync.service;

import io.github.anoobizzz.youget.YouGet;
import io.github.anoobizzz.youget.stock.StockRequest;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yousync.domain.PlaylistRequest;
import yousync.domain.PlaylistResponse;
import yousync.domain.Song;
import yousync.sources.MusicSource;
import yousync.ui.MainController;

import javax.ws.rs.ProcessingException;
import java.util.concurrent.ForkJoinPool;

@Service
public class CoreService {
    private static final Logger LOG = LoggerFactory.getLogger(CoreService.class);
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(4);

    @Autowired
    private MainController mainController;

    @Autowired
    private YouGet downloadSevice;

    public void checkAuthorization(MusicSource musicSource, String playlist) {
        if (!musicSource.requiresAuthorization(playlist) || musicSource.isAuthorized()) {
            loadSongs(musicSource, new PlaylistRequest(playlist));
        }
        musicSource.authorize();
    }

    public void loadSongs(MusicSource musicSource, PlaylistRequest request) {
        try {
            PlaylistResponse response = musicSource.getPlaylist(request);
            do {
                ObservableList<Song> songs = response.getCurrentSongs();
                FORK_JOIN_POOL.execute(() -> mainController.loadContent(songs));
                response = musicSource.getPlaylist(new PlaylistRequest(request.getId(), response.getNextPageToken()));
            } while (response.hasNext());
        } catch (ProcessingException | IllegalStateException e) {
            LOG.error("An exception occurred during response entity processing: {}", e.getMessage());
        }
    }

    public void downloadSongs(ObservableList<Song> songs) {
        songs.forEach(song -> downloadSevice.downloadAsync(new StockRequest(song.getUrl(), true, false)));
    }
}
