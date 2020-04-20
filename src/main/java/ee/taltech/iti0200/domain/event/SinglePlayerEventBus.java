package ee.taltech.iti0200.domain.event;

public class SinglePlayerEventBus extends EventBus {

    public SinglePlayerEventBus() {
        super(null);
    }

    @Override
    protected void propagate(Event event) {
        Class<? extends Event> type = event.getClass();
        if (!subscribers.containsKey(type)) {
            return;
        }
        for (Subscriber<Event> subscriber: subscribers.get(type)) {
            if (event.isStopped()) {
                return;
            }
            subscriber.handle(event);
        }
    }

}
