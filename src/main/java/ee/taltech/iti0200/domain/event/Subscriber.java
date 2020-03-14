package ee.taltech.iti0200.domain.event;

public interface Subscriber<T extends Event> {

    void handle(T event);

}
