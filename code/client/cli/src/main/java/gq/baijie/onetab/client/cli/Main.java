package gq.baijie.onetab.client.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import gq.baijie.onetab.api.ProgressOrResult;
import gq.baijie.onetab.api.Result;
import gq.baijie.onetab.api.StorageService;
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
            result.getResult().result().getSections().forEach(section -> {
              section.getItems()/*.stream()
                  .filter(item->item.getTitle().contains("|"))*/
                  .forEach(item -> {
                    System.out.print(item.getLink() + " | ");
                    System.out.println(item.getTitle());
                  });
              // add an empty line after section's items
              System.out.println();
            });
          }
        });
  }

}
