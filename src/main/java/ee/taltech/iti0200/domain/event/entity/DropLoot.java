package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.physics.Vector;

public class DropLoot extends Event implements Message {

    private static final long serialVersionUID = 1L;

    private final Living victim;
    private final Vector location;

    public DropLoot(Living victim, Receiver receiver) {
        super(receiver);
        this.victim = victim;
        this.location = new Vector(victim.getBoundingBox().getCentre());
    }

    public Living getVictim() {
        return victim;
    }

    public Vector getLocation() {
        return location;
    }

}
