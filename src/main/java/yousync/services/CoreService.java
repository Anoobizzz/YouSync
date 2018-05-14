package yousync.services;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import yousync.domain.PlaylistRequest;
import yousync.domain.PlaylistResponse;
import yousync.domain.Song;
import yousync.sources.MusicSource;
import yousync.ui.MainController;

import javax.annotation.PostConstruct;
import javax.ws.rs.ProcessingException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Service
public class CoreService {
    public static final String DEFAULT_DEST = System.getProperty("user.dir") + "/downloads";
    private static final Logger LOG = LoggerFactory.getLogger(CoreService.class);
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(4);

    @Autowired
    private ConvertService convertService;

    @Autowired
    private MainController mainController;

    @PostConstruct
    private void init() {
        new File("downloads").mkdir();
    }

    public void checkAuthorization(MusicSource musicSource, String playlist) {
        if (musicSource.isAuthorized()) {
            loadSongs(musicSource, new PlaylistRequest(playlist));
        }
        musicSource.authorize();
    }

    public void loadSongs(MusicSource musicSource, PlaylistRequest request) {
        try {
            PlaylistResponse response = musicSource.getPlaylist(request);
            do {
                //TODO: Not all songs from pageable saved?
                ObservableList<Song> songs = response.getCurrentSongs();
                //TODO: Think of handling issues
                FORK_JOIN_POOL.execute(() -> mainController
                        .resolveActiveUIController()
                        .loadSongs(songs));
                response = musicSource.getPlaylist(new PlaylistRequest(request.getId(), response.getNextPageToken()));
            } while (response.hasNext());
        } catch (MalformedURLException e) {
            LOG.error("Incorrect URL: {}", e.getMessage());
        } catch (ProcessingException | IllegalStateException e) {
            LOG.error("An exception occurred during response entity processing: {}", e.getMessage());
        }
    }

    public void downloadSongs(MusicSource musicSource, ObservableList<Song> songs){
    }
}
