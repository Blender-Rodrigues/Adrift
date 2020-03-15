package ee.taltech.iti0200.domain.event;

public interface Subscriber<T> {

    void handle(T event);

}
