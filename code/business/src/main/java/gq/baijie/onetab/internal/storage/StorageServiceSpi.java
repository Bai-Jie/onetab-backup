package gq.baijie.onetab.internal.storage;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

public interface StorageServiceSpi {

  @Nonnull
  String getType();

  @Nonnull
  Observable<ProgressOrResult<Void, Throwable>> save(@Nonnull WebArchive webArchive);

  @Nonnull
  Observable<ProgressOrResult<WebArchive, Throwable>> retrieve();

}
