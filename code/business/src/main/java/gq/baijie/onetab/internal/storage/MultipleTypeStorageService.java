package gq.baijie.onetab.internal.storage;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

@Singleton
public class MultipleTypeStorageService implements StorageService {

  private final StorageServiceRegister register;

  @Inject
  public MultipleTypeStorageService(StorageServiceRegister register) {
    this.register = register;
  }

  @Override
  public Observable<ProgressOrResult<Void, Throwable>> save(
      @Nonnull String type, @Nonnull WebArchive webArchive) {
    final StorageServiceSpi spi = register.getSpi(type);
    if (spi == null) {
      return Observable.error(new UnsupportedOperationException("unknown type: " + type));
    }
    return spi.save(webArchive);
  }

  @Override
  public Observable<ProgressOrResult<WebArchive, Throwable>> retrieve(@Nonnull String type) {
    final StorageServiceSpi spi = register.getSpi(type);
    if (spi == null) {
      return Observable.error(new UnsupportedOperationException("unknown type: " + type));
    }
    return spi.retrieve();
  }

}
