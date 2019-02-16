package Dance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class startScreen extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("startScreen.fxml"));
        Scene scene = new Scene(root, 1260, 700);
        stage.setScene(scene);
        stage.setTitle("Dance v1.0");
        stage.setResizable(false);
        stage.show();
    }
}
