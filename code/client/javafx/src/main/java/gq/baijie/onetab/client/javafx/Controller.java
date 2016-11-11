package gq.baijie.onetab.client.javafx;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.WebArchive;
import gq.baijie.onetab.client.javafx.eventbus.EventBus;
import gq.baijie.onetab.client.javafx.eventbus.LoadWebArchiveEvent;
import gq.baijie.onetab.client.javafx.eventbus.ShowWebArchiveEvent;

@Singleton
public class Controller {

  private final EventBus eventBus;

  private final StorageService storageService;

  @Inject
  public Controller(EventBus eventBus, StorageService storageService) {
    this.eventBus = eventBus;
    this.storageService = storageService;
    init();
  }

  private void init() {
    eventBus.events()
        .filter(event -> event instanceof LoadWebArchiveEvent)
        .cast(LoadWebArchiveEvent.class)
        .subscribe(this::onReceiveLoadWebArchiveEvent);
  }

  private void onReceiveLoadWebArchiveEvent(LoadWebArchiveEvent event) {
    System.out.println("received LoadWebArchiveEvent");
    System.out.printf("path: %s, type: %s%n", event.path, event.type);
    storageService.open(event.type, Paths.get(event.path)).retrieve()
        .filter(ProgressOrResult::isResult)
        .subscribe(result -> {
          if (result.getResult().failed()) {
            result.getResult().cause().printStackTrace();
          } else {
            final WebArchive webArchive = result.getResult().result();
            eventBus.emitEvent(new ShowWebArchiveEvent(webArchive));
          }
        });
  }

}
