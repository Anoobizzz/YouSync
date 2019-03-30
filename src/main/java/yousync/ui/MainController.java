package yousync.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static javafx.application.Platform.runLater;

@Component
public class MainController extends AbstractUIController {
    @FXML
    public AnchorPane menuPane;
    @FXML
    public SplitPane innerSplitPane;
    @FXML
    public SplitPane outerSplitPane;
    @FXML
    public AnchorPane playerPane;

    private MenuController menuController;
    private TableViewController tableViewController;
    private PlayerController playerController;
    private Stage authStage;

    @FXML
    void initialize() {
        runLater(() -> authStage = new Stage());

        menuController.setMenuShowOnClickAction(e -> {
            final double target;
            if (menuPane.widthProperty().isEqualTo(170).get()) {
                target = 0;
                menuPane.setMinWidth(25);
            } else {
                target = 1;
                menuPane.setMinWidth(170);
            }
            final KeyValue keyValue = new KeyValue(innerSplitPane.getDividers().get(0).positionProperty(), target);
            final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(400), keyValue));
            timeline.play();
        });
    }

    @Autowired
    public MainController(final MenuController menuController, final TableViewController tableViewController,
                          final PlayerController playerController) {
        this.menuController = menuController;
        this.tableViewController = tableViewController;
        this.playerController = playerController;
    }

    public void displayWebAuthenticationWindow(String page) {
        final WebView webView = new WebView();
        webView.getEngine().loadContent(page);
        Scene scene = new Scene(webView);
        authStage.setScene(scene);
        authStage.show();
    }

    public void hideWebAuthenticationWindow() {
        runLater(() -> authStage.hide());
    }
}

