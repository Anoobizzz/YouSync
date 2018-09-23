package yousync.service;

import org.springframework.stereotype.Service;
import yousync.ui.MainController;

import javax.inject.Inject;
import java.io.File;

import static java.util.Optional.ofNullable;

@Service
public class DirectoryResolverService {
    private static final String DEFAULT_DEST = System.getProperty("user.dir") + "/downloads";
    private File musicDirectory;

    @Inject
    public DirectoryResolverService(String musicDirectory) {
        this.musicDirectory = new File(ofNullable(musicDirectory).orElse(DEFAULT_DEST));
        if (!this.musicDirectory.exists() && !this.musicDirectory.mkdir()) {
            MainController.showErrorWindow("Failed to initialize download directory");
        }
    }

    public File getDownloadDirectory() {
        return musicDirectory;
    }
}