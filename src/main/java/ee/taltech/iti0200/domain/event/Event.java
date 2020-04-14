package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.core.net.Protocol;

abstract public class Event implements Message {

    private static final long serialVersionUID = 1L;

    private boolean isStopped = false;
    protected Receiver receiver;

    public Event(Receiver receiver) {
        this.receiver = receiver;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }

    public Event setReceiver(Receiver receiver) {
        this.receiver = receiver;
        return this;
    }

}
