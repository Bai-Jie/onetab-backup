package gq.baijie.onetab.client.cli;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.Result;
import gq.baijie.onetab.WebArchive;

import static gq.baijie.onetab.StorageService.TYPE_SQLITE;
import static gq.baijie.onetab.client.cli.Main.Utils.getHost;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;


public class Main implements Runnable {

  @Nonnull
  final MainComponent component;

  @Nonnull
  final Configuration configuration;

  public Main(@Nonnull MainComponent component, @Nonnull Configuration configuration) {
    this.component = component;
    this.configuration = configuration;
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
    final MainComponent component = DaggerMainComponent.builder()
        .build();

    return new Main(component, config);
  }

  @Override
  public void run() {
    component.storageService().open(configuration.importType, configuration.importFilePath)
        .retrieve()
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
    component.storageService().open(TYPE_SQLITE, Paths.get("sample.db")).save(webArchive).subscribe(
        next -> {
//          System.out.println(next);
        });
  }

  private static void printResult(@Nonnull WebArchive webArchive) {
    if (webArchive.getSections().isEmpty()) {
      return;
    }
    Collections.sort(
        webArchive.getSections(),
        comparing(WebArchive.Section::getCreateDate, nullsFirst(comparing(o -> o))).reversed()
    );
    printSection(webArchive.getSections().get(0));
    webArchive.getSections().stream().skip(1).forEach(section -> {
      System.out.println();
      printSection(section);
    });
  }

  private static void printSection(@Nonnull WebArchive.Section section) {
//    section.getItems().stream().filter(it->it.getTitle().equals("gradle.org")).forEach(item -> {
//    section.getItems().stream().filter(it -> it.getTitle().equals(getHost(it.getLink()))).forEach(item -> {
    section.getItems().forEach(item -> {
      if (item.getTitle() != null && !item.getTitle().equals(getHost(item.getLink()))) {
        System.out.print(item.getLink() + " | ");
        System.out.println(item.getTitle());
      } else {
        System.out.println(item.getLink());
      }
    });
  }

  static class Utils {

    private final static Pattern PATTERN = Pattern.compile("\\w+://([\\w.]+)/");

    static String getHost(@Nonnull String link) {
      final Matcher matcher = PATTERN.matcher(link);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
        return "";
      }
    }
  }

}
