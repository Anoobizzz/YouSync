package yousync.ui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

@Component
public class TableViewController {

    @FXML
    private TableView tableView;
    @FXML
    private CheckBox selectAll;
    @FXML
    private TableColumn image;
    @FXML
    private TableColumn artist;
    @FXML
    private TableColumn title;
    @FXML
    private TableColumn selected;

    @FXML
    void initialize() {
        image.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        artist.maxWidthProperty().bind(tableView.widthProperty().multiply(0.325));
        title.maxWidthProperty().bind(tableView.widthProperty().multiply(0.325));
        selected.maxWidthProperty().bind(tableView.widthProperty().multiply(0.05));
    }
}
