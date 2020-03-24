package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;

public class HealthyBrain implements Brain {

    private Bot bot;
    private World world;
    private Goal active;

    public HealthyBrain(World world) {
        this.world = world;
    }

    public void bind(Bot bot) {
        this.bot = bot;
        this.active = new LookForPlayer(bot, world);
    }

    public void followGoal(long tick) {
        active.execute(tick);
    }

    @Override
    public void updateSensor(Sensor sensor, Vector direction, Entity other) {
        active.react(sensor, direction, other);
    }

}
