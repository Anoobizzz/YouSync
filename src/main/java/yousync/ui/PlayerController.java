package yousync.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static javafx.scene.media.MediaPlayer.Status.*;

@Component
public class PlayerController extends AbstractUIController {
    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private Button playPause;
    @FXML
    private Label currentTime;
    @FXML
    private Label elapsedRemainingTime;
    @FXML
    private Slider timeline;

    private TableViewController tableView;
    private MediaPlayer mediaPlayer;

    @FXML
    void initialize() {
    }

    @Autowired
    public PlayerController(final TableViewController tableView){
        this.tableView = requireNonNull(tableView);
    }

    public void onPlayPauseMouseClicked(final MouseEvent mouseEvent) {
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer(tableView.getSelectedSong());
        }
        final MediaPlayer.Status status = mediaPlayer.getStatus();
        if (READY == status) {
//        mediaPlayer = new MediaPlayer();
            mediaPlayer.play();
        } else if (PLAYING == status) {
            mediaPlayer.pause();
        }
    }

    public void onPreviousMouseClicked(final MouseEvent mouseEvent) {
        //retrieve previous song from table and play
    }

    public void onNextMouseClicked(final MouseEvent mouseEvent) {
        //retrieve next song from table and play

    }

    public void onTimelineMouseClicked(final MouseEvent mouseEvent) {
//        mediaPlayer.seek(new Duration(timeline.getBlockIncrement()));
    }
}
