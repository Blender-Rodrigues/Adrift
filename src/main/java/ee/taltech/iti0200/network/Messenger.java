package ee.taltech.iti0200.network;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Messenger {

    private final ConcurrentLinkedQueue<String> inbox = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> outbox = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean alive = new AtomicBoolean(true);

    protected LinkedList<String> readInbox() {
        LinkedList<String> content;
        synchronized(inbox) {
            content = new LinkedList<>(inbox);
            inbox.clear();
        }
        return content;
    }

    protected void writeInbox(String message) {
        inbox.add(message);
    }

    protected String readOutbox() {
        return outbox.poll();
    }

    protected void writeOutbox(LinkedList<String> data) {
        synchronized(outbox) {
            outbox.addAll(data);
        }
    }

    public void terminate() {
        alive.set(false);
    }

    public boolean isAlive() {
        return alive.get();
    }

}
