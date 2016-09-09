package gq.baijie.onetab.internal.storage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class StorageServiceRegister {

  Map<String, StorageServiceSpi> storages = new HashMap<>();

  public void register(@Nonnull StorageServiceSpi spi) {
    storages.put(spi.getType(), spi);
  }

  @Nullable
  public StorageServiceSpi getSpi(@Nonnull String type) {
    return storages.get(type);
  }

}
