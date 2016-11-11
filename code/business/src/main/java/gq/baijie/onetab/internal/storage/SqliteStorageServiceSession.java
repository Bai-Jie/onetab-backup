package gq.baijie.onetab.internal.storage;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Results;
import gq.baijie.onetab.StorageServiceSession;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

import static gq.baijie.onetab.StorageService.TYPE_SQLITE;

public class SqliteStorageServiceSession implements StorageServiceSession {

  private static final String DATABASE_URL_PREFIX = "jdbc:sqlite:";

  @Nonnull
  private final String databaseUrl;

  @Nonnull
  private final Path path;

  public SqliteStorageServiceSession(@Nonnull Path path) {
    this.path = path;
    databaseUrl = DATABASE_URL_PREFIX + path;
  }

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
      subscriber.onCompleted();
    });
  }

  @Nonnull
  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve() {
    return Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) {
        return;
      }
      final WebArchive result = doRetrieve();
      if (subscriber.isUnsubscribed()) {
        return;
      }
      if (result != null) {
        subscriber.onNext(ProgressOrResult.result(Results.<WebArchive, Throwable>succeed(result)));
      }
      subscriber.onCompleted();
    });
  }

  private void doSave(@Nonnull WebArchive webArchive) {
    try(final Connection connection = DriverManager.getConnection(databaseUrl);
        final Statement statement = connection.createStatement()) {
      connection.setAutoCommit(false);

      statement.executeUpdate("drop table if exists section");
      statement.executeUpdate("create table section (storage_id integer PRIMARY KEY, id string, create_date integer DEFAULT NULL)");
      statement.executeUpdate("drop table if exists item");
      statement.executeUpdate("create table item (storage_id integer PRIMARY KEY, section_storage_id integer, id string, link string, title string)");
      for(WebArchive.Section section:webArchive.getSections()) {
        saveSection(section, connection);
      }

      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void saveSection(@Nonnull WebArchive.Section section, @Nonnull Connection connection)
      throws SQLException {
    try(final PreparedStatement statement = connection.prepareStatement(
        "insert into section (id, create_date) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, section.getId());
      if (section.getCreateDate() == null) {
        statement.setNull(2, Types.INTEGER);
      } else {
        statement.setLong(2, section.getCreateDate().getTime());
      }
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

  private static void saveItems(
      long sectionId, @Nonnull List<WebArchive.Item> items, @Nonnull Connection connection)
      throws SQLException {
    try(final PreparedStatement statement = connection.prepareStatement(
        "insert into item (section_storage_id, id, link, title) values (?, ?, ?, ?)")) {
      statement.setLong(1, sectionId);
      for(WebArchive.Item item: items){
        statement.setString(2, item.getId());
        statement.setString(3, item.getLink());
        statement.setString(4, item.getTitle());
        statement.executeUpdate();
      }
    }
  }

  @Nullable
  private WebArchive doRetrieve() {
    try (final Connection connection = DriverManager.getConnection(databaseUrl);
         final Statement statement = connection.createStatement()) {
      connection.setAutoCommit(false);

      final WebArchive.WebArchiveBuilder builder = WebArchive.builder();
      // retrieve sections
      ResultSet rs = statement.executeQuery("select storage_id, id, create_date from section");
      while (rs.next()) {
        final WebArchive.SectionBuilder section = builder.section();
        section.setId(rs.getString(2));
        final long time = rs.getLong(3);
        if (rs.wasNull()) {
          section.setCreateDate(null);
        } else {
          section.setCreateDate(new Date(time));
        }
        // retrieve the current section's items
        long sectionStorageId = rs.getLong(1);
        retrieveItems(sectionStorageId, section, connection);
      }
      rs.close();

      connection.commit();

      return builder.build();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void retrieveItems(
      long sectionStorageId, WebArchive.SectionBuilder section, @Nonnull Connection connection)
      throws SQLException {
    try (final PreparedStatement statement = connection.prepareStatement(
        "select id, link, title from item where section_storage_id is ?")) {
      statement.setLong(1, sectionStorageId);
      final ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        final WebArchive.ItemBuilder item = section.item();
        item.setId(resultSet.getString(1));
        item.setLink(resultSet.getString(2));
        item.setTitle(resultSet.getString(3));
      }
      resultSet.close();
    }
  }

}
