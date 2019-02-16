package Dance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class gameScreen extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gameScreen.fxml"));
        Scene scene = new Scene(root, 1260, 700);
        stage.setScene(scene);
        stage.setTitle("Dancing...");
        stage.setResizable(false);
        stage.show();
    }
}
