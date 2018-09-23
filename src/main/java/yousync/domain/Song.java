package yousync.domain;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import yousync.ui.AbstractUIController;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Song {
    private static final Image UNAVAILABLE_IMAGE = new Image(
            AbstractUIController.class.getClassLoader().getResource("unavailable.jpg").toString(),
            100, 100, true, true);

    private String artist;
    private String title;
    private ImageView imageView;
    private CheckBox checkBox = new CheckBox();
    private URL url;

    public Song(String rawTitle, String cover, URL url) {
        parseArtistAndTitle(rawTitle);
        this.url = url;
        this.imageView = new ImageView(resolveImage(cover));
    }

    public Song(String artist, String title, byte[] cover) {
        this.artist = artist;
        this.title = title;
        this.imageView = new ImageView(resolveImage(cover));
    }

    private void parseArtistAndTitle(String rawTitle) {
        Pattern pattern = compile("^(.*) - (.*)$");
        Matcher matcher = pattern.matcher(rawTitle);
        if (matcher.find()) {
            this.title = matcher.group(1);
            this.artist = matcher.group(2);
        } else {
            this.title = rawTitle.replaceAll("[^a-zA-Z0-9\\S]", " ");
        }
    }

    private static Image resolveImage(String url) {
        Image image;
        if (isEmpty(url)) {
            return UNAVAILABLE_IMAGE;
        }
        image = new Image(url, 100, 100, true, true, true);
        return image.isError() ? UNAVAILABLE_IMAGE : image;
    }

    private static Image resolveImage(byte[] bytes) {
        Image image;
        if (bytes == null) {
            return UNAVAILABLE_IMAGE;
        }
        image = new Image(new ByteArrayInputStream(bytes), 100, 100, true, true);
        return image.isError() ? UNAVAILABLE_IMAGE : image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
