package gq.baijie.onetab.client.javafx;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import static gq.baijie.onetab.StorageService.TYPE_DEFAULT;
import static gq.baijie.onetab.StorageService.TYPE_ONE_TAB_LOCAL_STORAGE;
import static gq.baijie.onetab.StorageService.TYPE_SQLITE;

public class OpenDialogPresenter {

  private Main main;

  @FXML
  private GridPane rootView;

  @FXML
  private TextField fileView;

  @FXML
  private ChoiceBox<String> typeView;

  @FXML
  private void initialize() {
    typeView.getItems().addAll(TYPE_DEFAULT, TYPE_ONE_TAB_LOCAL_STORAGE, TYPE_SQLITE);
    typeView.setValue(TYPE_DEFAULT);
  }

  @FXML
  private void handleBrowseClicked() {
    FileChooser fileChooser = new FileChooser();
    final File file = fileChooser.showOpenDialog(main != null ? main.getPrimaryStage() : null);
    if (file != null) {
      fileView.setText(file.getPath());
    }
  }

  public void bind(Main main) {
    this.main = main;
  }

  public GridPane getRootView() {
    return rootView;
  }

  public String getFilePath() {
    return fileView.getText();
  }

  public String getType() {
    return typeView.getValue();
  }

}
