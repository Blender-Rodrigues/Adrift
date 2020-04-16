package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.HealingSource;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.core.net.Protocol;

import static java.lang.String.format;

public class Heal extends Event implements Message {

    private static final long serialVersionUID = 1L;

    private final HealingSource source;
    private final Damageable target;

    public Heal(HealingSource source, Damageable target, Receiver receiver) {
        super(receiver);
        this.source = source;
        this.target = target;
    }

    public HealingSource getSource() {
        return source;
    }

    public Damageable getTarget() {
        return target;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public String toString() {
        return format("HealDamage{%s -> %s}", source, target);
    }
}
