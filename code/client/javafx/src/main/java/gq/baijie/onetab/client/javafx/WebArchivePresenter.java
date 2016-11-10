package gq.baijie.onetab.client.javafx;

import gq.baijie.onetab.WebArchive;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class WebArchivePresenter {

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

}
