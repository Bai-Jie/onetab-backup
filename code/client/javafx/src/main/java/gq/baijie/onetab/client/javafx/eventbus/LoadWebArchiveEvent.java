package gq.baijie.onetab.client.javafx.eventbus;

public class LoadWebArchiveEvent implements Event {
  public final String path;
  public final String type;

  public LoadWebArchiveEvent(String path, String type) {
    this.path = path;
    this.type = type;
  }
}
