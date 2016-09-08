package gq.baijie.onetab.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;

import gq.baijie.onetab.api.ProgressOrResult;
import gq.baijie.onetab.api.Result;
import gq.baijie.onetab.api.Results;
import gq.baijie.onetab.api.StorageService;
import gq.baijie.onetab.api.WebArchive;
import rx.Observable;

public class BasicStorageService implements StorageService {

  private static final String DEFAULT_CHARSET = "utf-8";


  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve(
      @Nonnull String type, @Nonnull InputStream input) {
    if (!TYPE_DEFAULT.equals(type)) {
      throw new UnsupportedOperationException();
    }
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
