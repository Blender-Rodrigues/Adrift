package ee.taltech.iti0200.network.message;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.List;

public class LoadWorld implements Message {

    private List<Entity> entities;
    private Receiver receiver;
    private Vector spawn;

    public LoadWorld(List<Entity> entities, Vector spawn, Receiver receiver) {
        this.entities = entities;
        this.spawn = spawn;
        this.receiver = receiver;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Vector getSpawn() {
        return spawn;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public Receiver getReceiver() {
        return receiver;
    }

}
