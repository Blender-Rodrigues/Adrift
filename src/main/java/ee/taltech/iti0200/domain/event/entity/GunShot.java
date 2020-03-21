package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.core.net.Protocol;

public class GunShot extends Event implements Message {

    private Gun gun;
    private Vector direction;

    public GunShot(Gun gun, Vector direction, Receiver receiver) {
        super(receiver);
        this.gun = gun;
        this.direction = direction;
    }

    public Gun getGun() {
        return gun;
    }

    public Vector getDirection() {
        return direction;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
