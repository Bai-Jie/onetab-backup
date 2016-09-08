package gq.baijie.onetab.api;

public interface Result<T, E> {

  boolean succeeded();

  T result();

  default boolean failed() {
    return !succeeded();
  }

  E cause();

}
