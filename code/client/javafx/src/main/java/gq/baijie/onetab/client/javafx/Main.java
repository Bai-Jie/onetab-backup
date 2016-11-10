package gq.baijie.onetab.client.javafx;

import java.io.IOException;

import javax.annotation.Nullable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    final Scene scene = createScene();
    if (scene != null) {
      primaryStage.setScene(scene);
    }
    primaryStage.setTitle("OneTab Backup");
    primaryStage.show();
  }

  @Nullable
  private Scene createScene() {
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Main.class.getResource("RootLayout.fxml"));
      BorderPane rootLayout = fxmlLoader.load();
      return new Scene(rootLayout);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
