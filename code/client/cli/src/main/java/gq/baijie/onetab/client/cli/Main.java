package gq.baijie.onetab.client.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Result;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.WebArchive;
import gq.baijie.onetab.internal.storage.BasicStorageService;
import gq.baijie.onetab.internal.storage.OneTabLocalStorageService;
import gq.baijie.onetab.internal.storage.SqliteStorageSpi;
import gq.baijie.onetab.internal.storage.StorageModule;
import gq.baijie.onetab.internal.storage.StorageServiceSpi;

public class Main implements Runnable {

  @Nonnull
  final MainComponent component;

  @Nonnull
  final String importType;

  public Main(@Nonnull MainComponent component, @Nonnull String importType) {
    this.component = component;
    this.importType = importType;
  }

  public static void main(String[] args) {
    final Result<Configuration, Configurations.FromArgumentsFailure> config =
        Configurations.fromArguments(args);
    if (config.failed()) {
      System.out.printf("args error [%s]: %s%n", config.cause().type, config.cause().cause);
      return;
    }

    create(config.result()).run();
  }

  private static Main create(@Nonnull Configuration config) {
    StorageServiceSpi importStorageSpi;

    if (StorageService.TYPE_ONE_TAB_LOCAL_STORAGE.equals(config.importType)) {
      importStorageSpi = new OneTabLocalStorageService(config.importFilePath);
    } else {
      try {
        final InputStream input = Files.newInputStream(config.importFilePath);
        importStorageSpi = new BasicStorageService(input);
      } catch (IOException e) {
        throw new RuntimeException("Cannot open open", e);
      }
    }

    final MainComponent component = DaggerMainComponent.builder()
        .storageModule(StorageModule.from(importStorageSpi, new SqliteStorageSpi()))
        .build();

    return new Main(component, config.importType);
  }

  @Override
  public void run() {
    component.storageService().retrieve(importType)
        .filter(ProgressOrResult::isResult)
        .subscribe(result -> {
          if (result.getResult().failed()) {
            result.getResult().cause().printStackTrace();
          } else {
            final WebArchive webArchive = result.getResult().result();
            printResult(webArchive);
            saveToSqliteDatabase(webArchive);
          }
        });
  }

  private void saveToSqliteDatabase(@Nonnull WebArchive webArchive) {
    component.storageService().save(StorageService.TYPE_SQLITE, webArchive).subscribe(next->{
      System.out.println(next);
    });
  }

  private static void printResult(@Nonnull WebArchive webArchive) {
    if (webArchive.getSections().isEmpty()) {
      return;
    }
    printSection(webArchive.getSections().get(0));
    webArchive.getSections().stream().skip(1).forEach(section -> {
      System.out.println();
      printSection(section);
    });
  }

  private static void printSection(@Nonnull WebArchive.Section section) {
    section.getItems().forEach(item -> {
      if (item.getTitle() != null) {
        System.out.print(item.getLink() + " | ");
        System.out.println(item.getTitle());
      } else {
        System.out.println(item.getLink());
      }
    });
  }

}
