package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;

public interface Brain {

    void bind(Bot bot);

    void followGoal(long tick);

    void updateSensor(Sensor sensor, Vector direction, Entity other);

    void kill();

}
