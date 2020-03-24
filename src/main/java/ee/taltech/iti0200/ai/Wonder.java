package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;

public class Wonder extends Goal {

    private double speed = 0.1;
    private double towards = speed;

    public Wonder(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(towards, 0));
    }

    @Override
    public void react(Sensor sensor, Vector direction, Entity other) {
        if (sensor != Sensor.TACTILE || !(other instanceof Terrain)) {
            return;
        }

        boolean isSideways = Math.abs(direction.getX()) > (1 / Math.sqrt(2));
        if (!isSideways) {
            return;
        }

        towards = direction.getX() > 0 ? -speed : speed;
    }

}
