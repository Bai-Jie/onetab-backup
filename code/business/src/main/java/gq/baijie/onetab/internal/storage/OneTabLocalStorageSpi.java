package gq.baijie.onetab.internal.storage;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.onetab.StorageServiceSession;

import static gq.baijie.onetab.StorageService.TYPE_ONE_TAB_LOCAL_STORAGE;

public class OneTabLocalStorageSpi implements StorageServiceSpi {

  @Nonnull
  @Override
  public String getType() {
    return TYPE_ONE_TAB_LOCAL_STORAGE;
  }

  @Override
  public StorageServiceSession open(@Nonnull Path path) {
    return new OneTabLocalStorageServiceSession(path);
  }

}
