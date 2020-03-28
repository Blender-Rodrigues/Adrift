package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;

import java.util.TreeMap;

public class InertBrain implements Brain {

    @Override
    public void bind(Bot bot, TreeMap<Long, Goal> goals, Runnable onDeath) {
        // Not used on client side
    }

    @Override
    public void followGoal(long tick) {
        // Not used on client side
    }

    @Override
    public void updateSensor(Sensor sensor, Vector direction, Entity other) {
        // May play some visual queues on client side like turning a head or listening animation
    }

    @Override
    public void kill() {
        // Not used on client side
    }

}
