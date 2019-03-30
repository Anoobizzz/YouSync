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
import yousync.ui.TableViewController;

import javax.ws.rs.ProcessingException;
import java.util.concurrent.ForkJoinPool;

@Service
public class CoreService {
    private static final Logger LOG = LoggerFactory.getLogger(CoreService.class);
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(4);

    @Autowired
    private TableViewController tableView;
    @Autowired
    private YouGet downloadSevice;
    @Autowired
    private SourceResolverService sourceResolver;

    public void checkAuthorization(final String playlistId, final String clientId, final String clientSecret) {
        final MusicSource source = sourceResolver.resolveResource(playlistId);
        final PlaylistRequest request = new PlaylistRequest(playlistId, clientId, clientSecret);
        if (!source.requiresAuthorization(playlistId) || source.isAuthorized()) {
            loadSongs(source, request);
        } else {
            source.authorize(clientId).thenRun(() -> loadSongs(source, request));
        }
    }

    private void loadSongs(final MusicSource musicSource, final PlaylistRequest request) {
        try {
            loadSongs(musicSource, request, musicSource.getPlaylist(request));
        } catch (ProcessingException | IllegalStateException e) {
            LOG.error("An exception occurred during response entity processing: {}", e.getMessage());
        }
    }

    private void loadSongs(final MusicSource musicSource, final PlaylistRequest request, final PlaylistResponse response) {
        FORK_JOIN_POOL.execute(() -> tableView.loadContent(response.getCurrentSongs()));
        if (response.hasNext()) {
            loadSongs(musicSource, request, musicSource.getPlaylist(request.setNextPageToken(response.getNextPageToken())));
        }
    }

    public void downloadSongs(ObservableList<Song> songs) {
        songs.forEach(song -> downloadSevice.downloadAsync(new StockRequest(song.getUrl(), true, false)));
    }
}
