package gq.baijie.onetab.client.cli;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import gq.baijie.onetab.Result;
import gq.baijie.onetab.Results;

public class Configurations {

  public static Result<Configuration, FromArgumentsFailure> fromArguments(@Nonnull String[] args) {
    if (args.length < 1) {
      return Results.fail(new FromArgumentsFailure(
          FromArgumentsFailure.Type.NO_REQUIRED_ARGUMENT, "no importFilePath"));
    }
    if (args.length > 1) {
      return Results.fail(new FromArgumentsFailure(FromArgumentsFailure.Type.UNKNOWN_FORMAT, ""));
    }
    final Path importFilePath;
    try {
      importFilePath = Paths.get(args[0]);
    } catch (InvalidPathException e) {
      return Results.fail(new FromArgumentsFailure(
          FromArgumentsFailure.Type.INVALID_PATH, e.getMessage()));
    }

    return Results.succeed(new Configuration(importFilePath));
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
