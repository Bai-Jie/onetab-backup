package gq.baijie.onetab.internal.storage;

import dagger.Module;
import dagger.Provides;
import gq.baijie.onetab.StorageService;

@Module
public class StorageModule {

  private final StorageServiceRegister register = new StorageServiceRegister();

  public static StorageModule from(StorageServiceSpi... spis) {
    StorageModule module = new StorageModule();
    for (StorageServiceSpi spi : spis) {
      module.register.register(spi);
    }
    return module;
  }

  @Provides
  StorageServiceRegister provideStorageServiceRegister() {
    return register;
  }

  @Provides
  StorageService provideStorageService(MultipleTypeStorageService service) {
    return service;
  }

}
