package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

public class DropLoot extends Event implements Message {

    public DropLoot(Receiver receiver) {
        super(receiver);
    }

}
