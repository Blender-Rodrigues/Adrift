package ee.taltech.iti0200.domain.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventBus {

    private final Logger logger = LogManager.getLogger(EventBus.class);

    protected HashMap<Class<? extends Event>, List<Subscriber<Event>>> subscribers = new HashMap<>();
    protected UUID id;

    private List<Event> queue = new LinkedList<>();

    public EventBus(UUID id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public void subscribe(Class<? extends Event> type, Subscriber<? extends Event> subscriber) {
        if (!subscribers.containsKey(type)) {
            subscribers.put(type, new LinkedList<>());
        }

        subscribers.get(type).add((Subscriber<Event>) subscriber);
    }

    public void unsubscribe(Class<? extends Event> type, Subscriber<? extends Event> subscriber) {
        if (!subscribers.containsKey(type)) {
            return;
        }
        subscribers.get(type).remove(subscriber);
    }

    public void dispatch(Event event) {
        queue.add(event);
    }

    public void propagateType(Class<? extends Event> type) {
        queue = queue.stream()
            .peek(event -> {
                if (type.isInstance(event)) {
                    propagate(event);
                }
            })
            .filter(event -> !type.isInstance(event))
            .collect(Collectors.toList());
    }

    public List<Event> propagateAll() {
        List<Event> sendToNetwork = new LinkedList<>(queue);

        queue.clear();

        return sendToNetwork.stream()
            .peek(this::propagate)
            .filter(event -> !event.isStopped())
            .collect(Collectors.toList());
    }

    protected void propagate(Event event) {
        if (event.getReceiver() == null) {
            logger.error("Event {} is missing a receiver", event.getClass());
            event.stop();
            return;
        }

        Class<? extends Event> type = event.getClass();
        if (!subscribers.containsKey(type)) {
            return;
        }

        for (Subscriber<Event> subscriber: subscribers.get(type)) {
            if (event.isStopped()) {
                return;
            }
            if (event.receiver.matches(id)) {
                subscriber.handle(event);
            }
        }
    }

}
