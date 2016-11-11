package gq.baijie.onetab.client.javafx.eventbus;

import gq.baijie.onetab.WebArchive;

public class ShowWebArchiveEvent implements Event {

  public final WebArchive webArchive;

  public ShowWebArchiveEvent(WebArchive webArchive) {
    this.webArchive = webArchive;
  }

}
