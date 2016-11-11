package gq.baijie.onetab.client.javafx;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nullable;

import gq.baijie.onetab.WebArchive;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Pair;

public class WebArchivePresenter {

  private Main main;

  @FXML
  private TreeTableView<WebArchive.Item> webArchiveTreeTableView;

  @FXML
  private TreeTableColumn<WebArchive.Item, String> linkTreeTableColumn;

  @FXML
  private TreeTableColumn<WebArchive.Item, String> titleTreeTableColumn;

  @FXML
  private void initialize() {
    linkTreeTableColumn.setCellValueFactory(
        param -> new ReadOnlyStringWrapper(param.getValue().getValue().getLink()));
    titleTreeTableColumn.setCellValueFactory(
        param -> new ReadOnlyStringWrapper(param.getValue().getValue().getTitle()));
  }

  public void bind(Main main) {
    this.main = main;
  }

  public void setWebArchive(WebArchive webArchive) {
    final TreeItem<WebArchive.Item> root =
        new TreeItem<>(new WebArchive.ItemBuilder().setLink("Sections").build());
    webArchive.getSections().forEach(section -> {
      final TreeItem<WebArchive.Item> sectionItem = new TreeItem<>(
          new WebArchive.ItemBuilder().setLink(section.getCreateDate().toString()).build());
      section.getItems().forEach(item -> {
        sectionItem.getChildren().add(new TreeItem<>(item));
      });
      root.getChildren().add(sectionItem);
    });

    webArchiveTreeTableView.setRoot(root);
  }

  @FXML
  private void handleOpenFile() {
    OpenDialogPresenter dialogContent = createDialogContentView();
    if (dialogContent == null) {
      return;
    }
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Open File");
    // buttons
    ButtonType openButtonType = new ButtonType("Open", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CANCEL);
    // content
    dialog.getDialogPane().setContent(dialogContent.getRootView());
    dialog.setResultConverter(button -> {
      if (button == openButtonType) {
        return new Pair<>(dialogContent.getFilePath(), dialogContent.getType());
      } else {
        return null;
      }
    });

    final Optional<Pair<String, String>> result = dialog.showAndWait();
    result.ifPresent(System.out::println);//TODO open business
  }

  @Nullable
  private OpenDialogPresenter createDialogContentView() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("OpenDialog.fxml"));
      fxmlLoader.load();
      final OpenDialogPresenter controller = fxmlLoader.getController();
      controller.bind(main);
      return controller;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
