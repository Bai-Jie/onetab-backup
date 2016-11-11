package gq.baijie.onetab.client.javafx;

import java.io.IOException;

import javax.annotation.Nullable;

import gq.baijie.onetab.client.javafx.eventbus.ShowWebArchiveEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

  private final MainComponent mainComponent;

  private Stage primaryStage;

  private WebArchivePresenter webArchivePresenter;

  {
    mainComponent = DaggerMainComponent.create();
    mainComponent.controller();
  }

  public static void main(String[] args) {
    launch(args);
  }

  private void receiveEvents() {
    mainComponent.eventBus().events()
        .filter(e -> e instanceof ShowWebArchiveEvent)
        .cast(ShowWebArchiveEvent.class)
        .subscribe(e -> {
          if (webArchivePresenter != null) {
            webArchivePresenter.setWebArchive(e.webArchive);
          }
        });
  }

  @Override
  public void start(Stage primaryStage) {
    receiveEvents();

    this.primaryStage = primaryStage;

    final Pair<Scene, WebArchivePresenter> scene = createScene();
    if (scene != null) {
      webArchivePresenter = scene.b;
      primaryStage.setScene(scene.a);
    }
    primaryStage.setTitle("OneTab Backup");
    primaryStage.show();

  }

  @Nullable
  private Pair<Scene, WebArchivePresenter> createScene() {
    try {
      final FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(Main.class.getResource("RootLayout.fxml"));
      BorderPane rootLayout = fxmlLoader.load();
      final WebArchivePresenter controller = fxmlLoader.getController();
      controller.bind(this);
      return Pair.of(new Scene(rootLayout), controller);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public MainComponent getMainComponent() {
    return mainComponent;
  }

  private static class Pair<A, B> {

    final A a;
    final B b;

    private Pair(A a, B b) {
      this.a = a;
      this.b = b;
    }

    static <A, B> Pair<A, B> of(A a, B b) {
      return new Pair<>(a, b);
    }
  }

}
