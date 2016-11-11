package gq.baijie.onetab.client.javafx.eventbus;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class EventBus {

  private final PublishSubject<Event> eventBus = PublishSubject.create();

  @Inject
  public EventBus() {
  }

  public void emitEvent(Event event) {
    eventBus.onNext(event);
  }

  public Observable<Event> events() {
    return eventBus.asObservable();
  }

}
