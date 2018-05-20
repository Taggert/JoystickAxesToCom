package taggert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;

/**
 * made by taggert
 */


public class App extends Application {

    static GuiController controller;
    static StarterClass mainThread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Joystick axes to COM");
        primaryStage.getIcons().add(new Image("/logoeng_small.png"));
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().addAll("/style.css");
        primaryStage.setScene(scene);
        mainThread = new StarterClass();
        primaryStage.setOnShown(e -> mainThread.start());
        primaryStage.setOnCloseRequest(e -> end(mainThread));
        primaryStage.show();
    }

    @SneakyThrows
    public static void main(String[] args) {
        launch(args);
    }

    private static void end(Thread mainThread) {
        while (mainThread.isAlive()) {
            mainThread.interrupt();
        }

    }


}
