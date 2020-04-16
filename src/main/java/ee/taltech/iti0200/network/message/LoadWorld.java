package ee.taltech.iti0200.network.message;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.List;

import static java.lang.String.format;

public class LoadWorld implements Message {

    private static final long serialVersionUID = 1L;

    private final List<Entity> entities;
    private final Receiver receiver;
    private final Vector spawn;

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

    @Override
    public String toString() {
        return format("LoadWorld{spawn=%s, entities=%s}", spawn, entities.size());
    }

}
