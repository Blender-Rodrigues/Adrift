package ee.taltech.iti0200.network.message;

import ee.taltech.iti0200.domain.entity.Entity;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.List;
import java.util.UUID;

public class LoadWorld implements Message {

    private List<Entity> entities;
    private UUID target;

    public LoadWorld(List<Entity> entities, UUID target) {
        this.entities = entities;
        this.target = target;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public boolean deliverTo(UUID id) {
        return target.equals(id);
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
