package gq.baijie.onetab.internal.storage;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.StorageServiceSession;
import gq.baijie.onetab.WebArchive;
import rx.Observable;

public interface StorageServiceSpi {

  @Nonnull
  String getType();

  StorageServiceSession open(@Nonnull Path path);

}
