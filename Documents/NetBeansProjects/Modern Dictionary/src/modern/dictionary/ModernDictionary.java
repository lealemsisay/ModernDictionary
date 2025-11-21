package modern.dictionary;

import Controller.DictionaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ModernDictionary extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Use the correct path to your FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/dictionary.fxml"));
        Parent root = loader.load();
        
        DictionaryController controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Modern Dictionary");
        
        // Set to full screen
        
        stage.setMaximized(true); // Start maximized
        stage.setFullScreenExitHint(""); // Remove exit hint
        // Optional: stage.setFullScreen(true); // For true fullscreen (press F11 to exit)
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}