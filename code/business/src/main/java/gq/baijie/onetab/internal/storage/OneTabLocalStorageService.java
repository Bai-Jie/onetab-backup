package gq.baijie.onetab.internal.storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Results;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

import static gq.baijie.onetab.StorageService.TYPE_ONE_TAB_LOCAL_STORAGE;

public class OneTabLocalStorageService implements StorageServiceSpi {

  private static final String DEFAULT_CHARSET = "utf-8";

  @Nonnull
  private final Path path;

  public OneTabLocalStorageService(@Nonnull Path path) {
    this.path = path;
  }


  @Nonnull
  @Override
  public String getType() {
    return TYPE_ONE_TAB_LOCAL_STORAGE;
  }

  @Nonnull
  @Override
  public Observable<ProgressOrResult<Void, Throwable>> save(@Nonnull WebArchive webArchive) {
    return null;
  }

  @Nonnull
  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve() {
    return Observable.create(subscriber -> {
      // do retrieve
      if (subscriber.isUnsubscribed()) {
        return;
      }
      WebArchive result = null;
      Throwable cause = null;
      try {
        result = doRetrieve();
      } catch (IOException e) {
        cause = e;
      }
      // send back result
      if (subscriber.isUnsubscribed()) {
        return;
      }
      if (result != null) {
        subscriber.onNext(ProgressOrResult.result(Results.<WebArchive, Throwable>succeed(result)));
      } else {
        subscriber.onNext(ProgressOrResult.result(Results.<WebArchive, Throwable>fail(cause)));
      }
    });
  }

  @Nonnull
  private WebArchive doRetrieve() throws IOException {
    JSONObject json = getJsonObject(getJsonString());
    WebArchive.WebArchiveBuilder builder = WebArchive.builder();
    retrieveWebArchive(json, builder);
    return builder.build();
  }

  private String getJsonString() throws IOException {
    return Files.lines(path, Charset.forName(DEFAULT_CHARSET)).collect(Collectors.joining("\n"));
  }

  @Nonnull
  private JSONObject getJsonObject(@Nonnull String jsonString) {
    return new JSONObject(jsonString);//TODO deal with UTF8 with BOM
  }

  private void retrieveWebArchive(
      @Nonnull JSONObject json, @Nonnull WebArchive.WebArchiveBuilder builder) {
    final JSONArray tabGroups = json.getJSONArray("tabGroups");
    for (int i = 0; i < tabGroups.length(); i++) {
      retrieveSection(tabGroups.getJSONObject(i), builder.section());
    }
  }

  private void retrieveSection(
      @Nonnull JSONObject json, @Nonnull WebArchive.SectionBuilder builder) {
    builder.setId(json.get("id").toString());
    builder.setCreateDate(new Date(json.getLong("createDate")));
    final JSONArray tabsMeta = json.getJSONArray("tabsMeta");
    for (int i = 0; i < tabsMeta.length(); i++) {
      retrieveItem(tabsMeta.getJSONObject(i), builder.item());
    }
  }

  private void retrieveItem(@Nonnull JSONObject json, @Nonnull WebArchive.ItemBuilder builder) {
    builder.setId(json.get("id").toString());
    builder.setLink(json.getString("url"));
    builder.setTitle(json.getString("title"));//TODO no title
  }

}
