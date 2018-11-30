package yousync.service;

import org.springframework.stereotype.Service;
import yousync.ui.MainController;

import java.io.File;

import static java.util.Optional.ofNullable;

@Service
public class ConfigurationService {
    private static final File DEFAULT_DOWNLOAD_DIR = new File(System.getProperty("user.dir") + "/downloads");
    private File musicDirectory;

    //TODO: Load configs from json if exists
    public ConfigurationService() {
        this.musicDirectory = ofNullable(musicDirectory).orElse(DEFAULT_DOWNLOAD_DIR);
        if (!this.musicDirectory.exists() && !this.musicDirectory.mkdir()) {
            MainController.showErrorWindow("Failed to initialize download directory");
        }
    }

    public File getDownloadDirectory() {
        return musicDirectory;
    }
}