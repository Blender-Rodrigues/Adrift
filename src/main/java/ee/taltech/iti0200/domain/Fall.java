package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.DamageSource;

import java.io.Serializable;

import static java.lang.String.format;

public class Fall implements DamageSource, Serializable {

    private static final long serialVersionUID = 1L;

    private final int damage;

    public Fall(int damage) {
        this.damage = damage;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return format("Fall{%d}", damage);
    }

}
