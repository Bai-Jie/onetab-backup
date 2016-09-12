package gq.baijie.onetab.client.cli;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import gq.baijie.onetab.Result;
import gq.baijie.onetab.Results;

import static gq.baijie.onetab.client.cli.Configurations.FromArgumentsFailure.Type.UNKNOWN_FORMAT;

public class Configurations {

  /**
   * <ul>
   *   <li>importFilePath</li>
   *   <li>--import-type type importFilePath</li>
   * </ul>
   */
  public static Result<Configuration, FromArgumentsFailure> fromArguments(@Nonnull String[] args) {
    if (args.length < 1) {
      return Results.fail(new FromArgumentsFailure(
          FromArgumentsFailure.Type.NO_REQUIRED_ARGUMENT, "no importFilePath"));
    }
    if (args.length == 2 || args.length > 3) {
      return Results.fail(new FromArgumentsFailure(UNKNOWN_FORMAT, ""));
    }
    Configuration.Builder builder = Configuration.builder();
    // * read importFileType
    if (args.length == 3) {
      if (!args[0].equalsIgnoreCase("--import-type")) {
        return Results.fail(new FromArgumentsFailure(UNKNOWN_FORMAT, "unknown switch: " + args[0]));
      }
      builder.setImportType(args[1]);
    }
    // * read importFilePath
    final Path importFilePath;
    try {
      importFilePath = Paths.get(args[args.length - 1]);
    } catch (InvalidPathException e) {
      return Results.fail(new FromArgumentsFailure(
          FromArgumentsFailure.Type.INVALID_PATH, e.getMessage()));
    }
    builder.setImportFilePath(importFilePath);

    return Results.succeed(builder.build());
  }

  public static class FromArgumentsFailure {

    final Type type;
    final String cause;

    public FromArgumentsFailure(Type type, String cause) {
      this.type = type;
      this.cause = cause;
    }

    public enum Type {
      UNKNOWN_FORMAT,
      NO_REQUIRED_ARGUMENT,
      INVALID_PATH
    }
  }

}
