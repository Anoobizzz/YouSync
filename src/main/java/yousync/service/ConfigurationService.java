package yousync.service;

import org.springframework.stereotype.Service;
import yousync.ui.AlertController;

import java.io.File;
import java.util.Optional;

@Service
public class ConfigurationService {
    private static final String DEFAULT_DOWNLOAD_DIR_PATH = System.getProperty("user.dir") + "/downloads";
    private File musicDirectory;

    public ConfigurationService(final String customDirectory) {
        this.musicDirectory = new File(Optional.ofNullable(customDirectory).orElse(DEFAULT_DOWNLOAD_DIR_PATH));
        if (!this.musicDirectory.exists() && !this.musicDirectory.mkdir()) {
            AlertController.showErrorWindow("Failed to initialize download directory");
        }
    }

    public File getDownloadDirectory() {
        return musicDirectory;
    }
}