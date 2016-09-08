package gq.baijie.onetab.api;

import java.io.InputStream;

import javax.annotation.Nonnull;

import rx.Observable;

public interface StorageService {

  public static final String TYPE_DEFAULT = "default";

  Observable<ProgressOrResult<WebArchive, Throwable>> retrieve(
      @Nonnull String type, @Nonnull InputStream input);

}
