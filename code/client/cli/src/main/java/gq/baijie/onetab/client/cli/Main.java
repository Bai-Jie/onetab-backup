package gq.baijie.onetab.client.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.annotation.Nonnull;

import gq.baijie.onetab.api.ProgressOrResult;
import gq.baijie.onetab.api.Result;
import gq.baijie.onetab.api.StorageService;
import gq.baijie.onetab.api.WebArchive;
import gq.baijie.onetab.impl.BasicStorageService;

public class Main {

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

    final StorageService storageService = new BasicStorageService();
    storageService.retrieve(StorageService.TYPE_DEFAULT, input)
        .filter(ProgressOrResult::isResult)
        .subscribe(result -> {
          if (result.getResult().failed()) {
            result.getResult().cause().printStackTrace();
          } else {
            printResult(result.getResult().result());
          }
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
