package gq.baijie.onetab;

import java.nio.file.Path;

import javax.annotation.Nonnull;

public interface StorageService {

  String TYPE_DEFAULT = "default";
  String TYPE_ONE_TAB_LOCAL_STORAGE = "OneTab Local Storage";
  String TYPE_SQLITE = "SQLite";

  StorageServiceSession open(@Nonnull String type, @Nonnull Path path);

}
