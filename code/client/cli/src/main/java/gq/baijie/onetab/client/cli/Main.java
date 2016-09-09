package gq.baijie.onetab.client.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Result;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.WebArchive;
import gq.baijie.onetab.internal.storage.BasicStorageService;
import gq.baijie.onetab.internal.storage.SqliteStorageSpi;
import gq.baijie.onetab.internal.storage.StorageModule;

public class Main implements Runnable {

  final MainComponent component;

  public Main(@Nonnull InputStream input) {
    component = DaggerMainComponent.builder()
        .storageModule(StorageModule.from(new BasicStorageService(input), new SqliteStorageSpi()))
        .build();
  }

  public static void main(String[] args) {
    final Result<Configuration, Configurations.FromArgumentsFailure> config =
        Configurations.fromArguments(args);
    if (config.failed()) {
      System.out.printf("args error [%s]: %s%n", config.cause().type, config.cause().cause);
      return;
    }

    final InputStream input;
    try {
      input = Files.newInputStream(config.result().importFilePath);
    } catch (IOException e) {
      System.out.println("Cannot open open: " + e.getMessage());
      return;
    }

    new Main(input).run();
  }

  @Override
  public void run() {
    component.storageService().retrieve(StorageService.TYPE_DEFAULT)
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
