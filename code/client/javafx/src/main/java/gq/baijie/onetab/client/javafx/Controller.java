package gq.baijie.onetab.client.javafx;

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.onetab.ProgressOrResult;
import gq.baijie.onetab.StorageService;
import gq.baijie.onetab.WebArchive;
import rx.subjects.PublishSubject;

@Singleton
public class Controller {

  private final PublishSubject<Event> eventBus = PublishSubject.create();

  private final StorageService storageService;

  { // init
    eventBus
        .filter(event -> event instanceof LoadWebArchiveEvent)
        .cast(LoadWebArchiveEvent.class)
        .subscribe(this::onReceiveLoadWebArchiveEvent);
  }

  @Inject
  public Controller(StorageService storageService) {
    this.storageService = storageService;
  }

  public void emitEvent(Event event) {
    eventBus.onNext(event);
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
            System.out.println(webArchive);//TODO show it in UI
          }
        });
  }

  public interface Event {

  }

  public static class LoadWebArchiveEvent implements Event {
    public final String path;
    public final String type;

    public LoadWebArchiveEvent(String path, String type) {
      this.path = path;
      this.type = type;
    }
  }

}
