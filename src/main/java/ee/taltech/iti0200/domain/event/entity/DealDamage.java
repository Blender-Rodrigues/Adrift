package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.core.net.Protocol;

import static java.lang.String.format;

public class DealDamage extends Event implements Message {

    private DamageSource source;
    private Damageable target;

    public DealDamage(DamageSource source, Damageable target, Receiver receiver) {
        super(receiver);
        this.source = source;
        this.target = target;
    }

    public DamageSource getSource() {
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
        return format("DealDamage{%s -> %s}", source, target);
    }

}
