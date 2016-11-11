package gq.baijie.onetab.internal.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.onetab.StorageServiceSession;

import static gq.baijie.onetab.StorageService.TYPE_DEFAULT;

public class BasicStorageSpi implements StorageServiceSpi {

  @Nonnull
  @Override
  public String getType() {
    return TYPE_DEFAULT;
  }

  @Override
  public StorageServiceSession open(@Nonnull Path path) {
    try {
      return new BasicStorageServiceSession(Files.newInputStream(path)); //TODO not use stream here
    } catch (IOException e) {
      throw new RuntimeException(e); //TODO improve this
    }
  }

}
