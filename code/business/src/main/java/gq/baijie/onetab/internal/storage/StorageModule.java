package gq.baijie.onetab.internal.storage;

import dagger.Module;
import dagger.Provides;
import gq.baijie.onetab.StorageService;

@Module
public class StorageModule {

  private final StorageServiceRegister register = new StorageServiceRegister();

  { // init
    register.register(new BasicStorageSpi());
    register.register(new OneTabLocalStorageSpi());
    register.register(new SqliteStorageSpi());
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
