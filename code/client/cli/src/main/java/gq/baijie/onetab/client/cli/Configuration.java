package gq.baijie.onetab.client.cli;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.onetab.StorageService;

public class Configuration {

  final String importType;

  final Path importFilePath;

  public Configuration(String importType, Path importFilePath) {
    this.importType = importType;
    this.importFilePath = importFilePath;
  }

  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private static final String DEFAULT_IMPORT_TYPE = StorageService.TYPE_DEFAULT;

    String importType;

    Path importFilePath;

    public Builder setImportType(String importType) {
      this.importType = importType;
      return this;
    }

    public Builder setImportFilePath(Path importFilePath) {
      this.importFilePath = importFilePath;
      return this;
    }

    public Configuration build() {
      if (importType == null) {
        importType = DEFAULT_IMPORT_TYPE;
      }
      return new Configuration(importType, importFilePath);
    }

  }

}
