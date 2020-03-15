package ee.taltech.iti0200.domain.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EventBus {

    private HashMap<Class<? extends Event>, List<Subscriber<Event>>> subscribers = new HashMap<>();
    private List<Event> queue = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public void subscribe(Class<? extends Event> type, Subscriber<? extends Event> subscriber) {
        if (!subscribers.containsKey(type)) {
            subscribers.put(type, new LinkedList<>());
        }

        subscribers.get(type).add((Subscriber<Event>) subscriber);
    }

    public void dispatch(Event event) {
        Class<? extends Event> type = event.getClass();
        if (!subscribers.containsKey(type)) {
            return;
        }
        queue.add(event);
    }

    public void propagate() {
        queue.forEach(event -> {
            Class<? extends Event> type = event.getClass();
            for (Subscriber<Event> subscriber: subscribers.get(type)) {
                if (event.isStopped()) {
                    return;
                }
                subscriber.handle(event);
            }
        });
        queue.clear();
    }

}
