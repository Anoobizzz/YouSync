package yousync;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
public class YouSyncApplication extends Application {
    private static final String APP_WINDOW_TITLE = "YouSync";
    private ConfigurableApplicationContext context;
    private Parent root;

    @Override
    public void init() throws IOException {
        context = SpringApplication.run(YouSyncApplication.class);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        loader.setControllerFactory(context::getBean);
        root = loader.load();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_WINDOW_TITLE);
        stage.setMinWidth(720);
        stage.setMinHeight(510);
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    public static void main(String[] args) {
        Application.launch(YouSyncApplication.class, args);
    }
}
