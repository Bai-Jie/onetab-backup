package gq.baijie.onetab.client.javafx;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Nullable;

import gq.baijie.onetab.WebArchive;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

  private final Controller controller;

  private Stage primaryStage;

  private static final WebArchive SAMPLE_DATA;
  static {
    final WebArchive.WebArchiveBuilder builder = WebArchive.builder();
    WebArchive.SectionBuilder sectionBuilder;
    sectionBuilder = builder.section().setCreateDate(new Date()).setId("1");
    sectionBuilder.item().setLink("https://1.1").setTitle("1.1").setId("1.1");
    sectionBuilder.item().setLink("https://1.2").setTitle("1.2").setId("1.2");

    sectionBuilder = builder.section().setCreateDate(new Date()).setId("2");
    sectionBuilder.item().setLink("https://2.1").setTitle("2.1").setId("2.1");
    sectionBuilder.item().setLink("http://2.2").setTitle("2.2").setId("2.2");
    sectionBuilder.item().setLink("https://2.3").setTitle("2.3").setId("2.3");

    sectionBuilder = builder.section().setCreateDate(new Date()).setId("3");
    sectionBuilder.item().setLink("https://3.1").setTitle("3.1").setId("3.1");
    SAMPLE_DATA = builder.build();
  }

  {
    final MainComponent component = DaggerMainComponent.create();
    controller = component.newController();
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    final Pair<Scene, WebArchivePresenter> scene = createScene();
    if (scene != null) {
      scene.b.setWebArchive(SAMPLE_DATA);
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

  public Controller getController() {
    return controller;
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
