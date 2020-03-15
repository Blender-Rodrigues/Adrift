package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Messenger {

    private final ConcurrentLinkedQueue<Message> inbox;
    private final ConcurrentLinkedQueue<Message> outbox;
    private final AtomicBoolean alive;

    public Messenger(ConcurrentLinkedQueue<Message> inbox, ConcurrentLinkedQueue<Message> outbox, AtomicBoolean alive) {
        this.inbox = inbox;
        this.outbox = outbox;
        this.alive = alive;
    }

    public Messenger() {
        alive = new AtomicBoolean(true);
        outbox = new ConcurrentLinkedQueue<>();
        inbox = new ConcurrentLinkedQueue<>();
    }

    public LinkedList<Message> readInbox() {
        LinkedList<Message> content;
        synchronized(inbox) {
            content = new LinkedList<>(inbox);
            inbox.clear();
        }
        return content;
    }

    public void writeInbox(Message message) {
        inbox.add(message);
    }

    public Message readOutbox() {
        synchronized(outbox) {
            if (outbox.isEmpty()) {
                return null;
            }
            return outbox.poll();
        }
    }

    public void writeOutbox(Message message) {
        outbox.add(message);
    }

    public void writeOutbox(List<Message> messages) {
        synchronized(outbox) {
            outbox.addAll(messages);
        }
    }

    public void terminate() {
        alive.set(false);
    }

    public boolean isAlive() {
        return alive.get();
    }

}
