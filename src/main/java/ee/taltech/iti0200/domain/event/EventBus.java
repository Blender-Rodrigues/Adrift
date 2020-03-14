package ee.taltech.iti0200.domain.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EventBus {

    private HashMap<Class<? extends Event>, List<Subscriber<? super Event>>> subscribers = new HashMap<>();

    public void subscribe(Class<? extends Event> type, Subscriber<? super Event> subscriber) {
        if (!subscribers.containsKey(type)) {
            subscribers.put(type, new LinkedList<>());
        }
        subscribers.get(type).add(subscriber);
    }

    public void dispatch(Event event) {
        Class<? extends Event> type = event.getClass();
        if (!subscribers.containsKey(type)) {
            return;
        }

        for (Subscriber<? super Event> subscriber: subscribers.get(type)) {
            if (event.isCancelled()) {
                return;
            }
            subscriber.handle(event);
        }
    }

}
