package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

public class DropLoot extends Event implements Message {

    private Living victim;

    public DropLoot(Living victim, Receiver receiver) {
        super(receiver);
        this.victim = victim;
    }

    public Living getVictim() {
        return victim;
    }

}
