package ee.taltech.iti0200.domain.event;

public class SinglePlayerEventBus extends EventBus {

    public SinglePlayerEventBus() {
        super(null);
    }

    @Override
    protected void propagate(Event event) {
        Class<? extends Event> type = event.getClass();
        for (Subscriber<Event> subscriber: subscribers.get(type)) {
            if (event.isStopped()) {
                return;
            }
            subscriber.handle(event);
        }
    }

}
