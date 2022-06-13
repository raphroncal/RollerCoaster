package csopesy.group3.rollercoaster;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Driver extends Application {
    final int HEIGHT = 1200;
    final int WIDTH = 675;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader menuFxml = new FXMLLoader(Driver.class.getResource("menu.fxml"));
        Scene menuScene = new Scene(menuFxml.load(), HEIGHT, WIDTH);

        stage.setTitle("Roller Coaster Problem");
        stage.setResizable(false);
        stage.setScene(menuScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}