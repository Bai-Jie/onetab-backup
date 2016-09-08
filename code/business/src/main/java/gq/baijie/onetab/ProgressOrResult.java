package gq.baijie.onetab;

import javax.annotation.Nonnull;

public class ProgressOrResult<T, E> {

  final boolean isProgress;

  final Progress progress;

  final Result<T, E> result;

  @Nonnull
  public static <T, E> ProgressOrResult<T, E> progress(Progress progress) {
    return new ProgressOrResult<>(true, progress, null);
  }

  @Nonnull
  public static <T, E> ProgressOrResult<T, E> result(Result<T, E> result) {
    return new ProgressOrResult<>(false, null, result);
  }

  private ProgressOrResult(boolean isProgress, Progress progress, Result<T, E> result) {
    this.isProgress = isProgress;
    this.progress = progress;
    this.result = result;
  }

  public boolean isProgress() {
    return isProgress;
  }

  public Progress getProgress() {
    return progress;
  }

  public boolean isResult() {
    return !isProgress;
  }

  public Result<T, E> getResult() {
    return result;
  }

}
