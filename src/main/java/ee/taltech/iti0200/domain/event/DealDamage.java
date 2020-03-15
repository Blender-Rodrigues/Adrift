package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;

public class DealDamage extends Event implements Message {

    private DamageSource source;
    private Damageable target;

    public DealDamage(DamageSource source, Damageable target) {
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

}
