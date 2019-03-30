package yousync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import yousync.config.CommonConfiguration;

import java.io.IOException;

public class YouSyncApplication extends Application {
    private static final String APP_WINDOW_TITLE = "YouSync";
    private static final ApplicationContext CONTEXT = new AnnotationConfigApplicationContext(CommonConfiguration.class);

    public static void main(String[] args) {
        Application.launch(YouSyncApplication.class, args);
    }

    @Override
    public void start(final Stage stage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        fxmlLoader.setControllerFactory(CONTEXT::getBean);
        stage.setTitle(APP_WINDOW_TITLE);
        stage.setMinWidth(720);
        stage.setMinHeight(510);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
