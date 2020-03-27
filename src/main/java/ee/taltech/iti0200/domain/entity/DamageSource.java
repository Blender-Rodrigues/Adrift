package ee.taltech.iti0200.domain.entity;

public interface DamageSource {

    int getDamage();

    default Living getOwner() {
        return null;
    }

}
