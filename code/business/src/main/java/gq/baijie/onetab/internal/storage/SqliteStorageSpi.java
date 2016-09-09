package gq.baijie.onetab.internal.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Results;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

import static gq.baijie.onetab.StorageService.TYPE_SQLITE;

public class SqliteStorageSpi implements StorageServiceSpi {

  private static final String DATABASE_URL = "jdbc:sqlite:sample.db";

  @Nonnull
  @Override
  public String getType() {
    return TYPE_SQLITE;
  }

  @Nonnull
  @Override
  public Observable<ProgressOrResult<Void, Throwable>> save(@Nonnull WebArchive webArchive) {
    return Observable.create(subscriber -> {
      if(subscriber.isUnsubscribed()) {
        return;
      }
      doSave(webArchive);
      if (subscriber.isUnsubscribed()) {
        return;
      }
      subscriber.onNext(ProgressOrResult.result(Results.<Void, Throwable>succeed(null)));
    });
  }

  @Nonnull
  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve() {
    return null;
  }

  private void doSave(@Nonnull WebArchive webArchive) {
//    final Connection connection = DriverManager.getConnection(DATABASE_URL);
    try(final Connection connection = DriverManager.getConnection(DATABASE_URL);
        final Statement statement = connection.createStatement()) {
      connection.setAutoCommit(false);

      statement.executeUpdate("drop table if exists section");
      statement.executeUpdate("create table section (id integer PRIMARY KEY)");
      statement.executeUpdate("drop table if exists item");
      statement.executeUpdate("create table item (id integer PRIMARY KEY, section_id integer, link string, title string)");;
      for(WebArchive.Section section:webArchive.getSections()) {
        saveSection(section, connection);
      }

      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void saveSection(@Nonnull WebArchive.Section section, @Nonnull Connection connection)
      throws SQLException {
    try(final PreparedStatement statement = connection.prepareStatement(
//        "insert into section () values ()", Statement.RETURN_GENERATED_KEYS)) {
        "insert into section DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS)) {
      statement.executeUpdate();
      final ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        final long sectionId = generatedKeys.getLong(1);
        saveItems(sectionId, section.getItems(), connection);
      } else {
        throw new IllegalStateException("no generated primary key");
      }
    }
  }

  private void saveItems(
      long sectionId, @Nonnull List<WebArchive.Item> items, @Nonnull Connection connection)
      throws SQLException {
    try(final PreparedStatement statement = connection.prepareStatement(
        "insert into item (section_id, link, title) values (?, ?, ?)")) {
      statement.setLong(1, sectionId);
      for(WebArchive.Item item: items){
        statement.setString(2, item.getLink());
        statement.setString(3, item.getTitle());
        statement.executeUpdate();
      }
    }
  }

}
