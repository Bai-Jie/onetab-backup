package gq.baijie.onetab.internal.storage;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.onetab.StorageServiceSession;

import static gq.baijie.onetab.StorageService.TYPE_SQLITE;

public class SqliteStorageSpi implements StorageServiceSpi {

  @Nonnull
  @Override
  public String getType() {
    return TYPE_SQLITE;
  }

  @Override
  public StorageServiceSession open(@Nonnull Path path) {
    return new SqliteStorageServiceSession(path);
  }

}
