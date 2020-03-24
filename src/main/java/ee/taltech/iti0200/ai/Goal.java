package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;

import java.util.Random;

public abstract class Goal {

    protected static final Random RANDOM = new Random();

    protected Bot bot;
    protected World world;

    public Goal(Bot bot, World world) {
        this.bot = bot;
        this.world = world;
    }

    abstract public void execute(long tick);

    abstract public void react(Sensor sensor, Vector direction, Entity other);

    protected void move(Vector towards) {
        Vector acceleration = bot.getAcceleration();

        acceleration.add(towards);
        acceleration.scale(0.9);

        Vector speed = bot.getSpeed();
        speed.add(acceleration);
    }

}
