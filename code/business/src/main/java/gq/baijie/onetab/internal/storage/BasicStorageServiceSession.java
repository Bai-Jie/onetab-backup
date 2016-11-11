package gq.baijie.onetab.internal.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Result;
import gq.baijie.onetab.Results;
import gq.baijie.onetab.StorageServiceSession;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

import static gq.baijie.onetab.StorageService.TYPE_DEFAULT;

public class BasicStorageServiceSession implements StorageServiceSession {

  private static final String DEFAULT_CHARSET = "utf-8";

  @Nonnull
  private final InputStream input;

  public BasicStorageServiceSession(@Nonnull InputStream input) {
    this.input = input;
  }

  @Nonnull
  @Override
  public String getType() {
    return TYPE_DEFAULT;
  }

  @Override
  public Observable<ProgressOrResult<Void, Throwable>> save(@Nonnull WebArchive webArchive) {
    return Observable.error(new UnsupportedOperationException());
  }

  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve() {
    return Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) {
        return;
      }
      try {
        final WebArchive result =
            doRetrieve(new BufferedReader(new InputStreamReader(input, DEFAULT_CHARSET)));
        if (!subscriber.isUnsubscribed()) {
          final Result<WebArchive, Throwable> succeed = Results.succeed(result);
          subscriber.onNext(ProgressOrResult.result(succeed));
        }
      } catch (UnsupportedEncodingException e) {
        subscriber.onError(e);
      }
    });
  }

  private WebArchive doRetrieve(@Nonnull BufferedReader reader) {
    WebArchive.WebArchiveBuilder builder = WebArchive.builder();
    retrieveWebArchive(reader, builder);
    return builder.build();
  }

  private void retrieveWebArchive(
      @Nonnull BufferedReader reader, @Nonnull WebArchive.WebArchiveBuilder builder) {
//    while (!retrieveSection(reader, builder.section())) {}
    /*for(;;) {
      final boolean finished = retrieveSection(reader, builder.section());
    }*/
    boolean finished;
    do {
      finished = retrieveSection(reader, builder.section());
    } while (!finished);
  }

  /**
   * @return finished
   */
  private boolean retrieveSection(
      @Nonnull BufferedReader reader, @Nonnull WebArchive.SectionBuilder builder) {
    try {
      for (; ; ) {
        final String nextLine = reader.readLine();
        if (nextLine == null) {
          return true;
        }
        if (nextLine.isEmpty()) {
          return false;
        }
        retrieveItem(nextLine, builder.item());
      }
    } catch (IOException e) {
      e.printStackTrace(); //TODO
      return true;
    }
  }

  private void retrieveItem(@Nonnull String raw, @Nonnull WebArchive.ItemBuilder builder) {
    final String[] split = raw.split(" \\| ", 2);
    builder.setLink(split[0]);
    if (split.length >= 2) {
      builder.setTitle(split[1]);
    }
  }

}
