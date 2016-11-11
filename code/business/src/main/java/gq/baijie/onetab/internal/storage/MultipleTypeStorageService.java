package gq.baijie.onetab.internal.storage;

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.StorageServiceSession;
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
  public StorageServiceSession open(@Nonnull String type, @Nonnull Path path) {
    final StorageServiceSpi spi = register.getSpi(type);
    if (spi == null) {
      throw new UnsupportedOperationException("unknown type: " + type);
    }
    return spi.open(path);
  }

}
