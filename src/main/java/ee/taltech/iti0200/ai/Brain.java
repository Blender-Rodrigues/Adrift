package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;

import java.util.TreeMap;

public interface Brain {

    void bind(Bot bot, TreeMap<Long, Goal> goals, Runnable onDeath);

    void followGoal(long tick);

    void updateSensor(Sensor sensor, Vector direction, Entity other);

    void kill();

}
