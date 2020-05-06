package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.core.net.Protocol;

import static java.lang.String.format;

public class AddGun extends Event implements Message {

    private static final long serialVersionUID = 1L;

    private final Gun gun;
    private final Living target;

    public AddGun(Gun gun, Living target, Receiver receiver) {
        super(receiver);
        this.gun = gun;
        this.target = target;
    }

    public Gun getGun() {
        return gun;
    }

    public Living getTarget() {
        return target;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public String toString() {
        return format("Give gun{%s -> %s}", gun, target);
    }
}
