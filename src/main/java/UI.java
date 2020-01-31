import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UI extends Application {
    public static TasksDAO Data;

    // Переписать. Плохо
    public static void Launch(String[] args, TasksDAO realisation) {
        Data = realisation;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Design.fxml"));
        primaryStage.setTitle("TaskManager by AnWal");
        primaryStage.setScene(new Scene(root, 1280, 750));
        primaryStage.show();
    }
}