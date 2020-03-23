package ee.taltech.iti0200.network;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.message.Message;

import java.util.LinkedList;

import static ee.taltech.iti0200.application.Game.eventBus;

abstract public class Network implements Component {

    protected final World world;

    public Network(World world) {
        this.world = world;
    }

    abstract public void propagate(long tick);
    abstract protected Messenger getMessenger();

    @Override
    public void update(long tick) {
        LinkedList<Message> messages = getMessenger().readInbox();

        for (Message message: messages) {
            if (message instanceof Event) {
                eventBus.dispatch((Event) message);
            }
        }

        eventBus.propagateType(UpdateVector.class);
    }

    @Override
    public void terminate() {
        getMessenger().terminate();
    }

}
