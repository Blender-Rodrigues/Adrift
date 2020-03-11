package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

import java.util.*;

public class Bot extends Living {

    private static final Vector size = new Vector(1.5, 1.5);
    private static final double mass = 70.0;
    private static final Random random = new Random();
    private static final double elasticity = 0.25;
    private static final double frictionCoefficient = 0.99;
    private static final int shootRechargeTime = 250;
    private static final int bulletSpeed = 15;
    private World world;

    private Vector acceleration;
    private int ticksLeftForRecharge;

    public Bot(Vector position, World world) {
        super(new Body(mass, size, position, true, true), false);
        setElasticity(elasticity);
        setFrictionCoefficient(frictionCoefficient);
        acceleration = new Vector(0.0, 0.0);
        this.world = world;
        ticksLeftForRecharge = 0;
    }

    public void update(long tick) {
        if (tick % 10 == 0) {
            move();
            ticksLeftForRecharge -= 10;
            if (ticksLeftForRecharge <= 0) {
                look().ifPresent(this::shoot);
            }
        }
    }

    private void shoot(Map.Entry<Vector, Double> target) {
        if (target.getValue() < 0.2) {
            ticksLeftForRecharge = shootRechargeTime;

            Vector speed = new Vector(target.getKey());
            speed.normalize();
            speed.scale(bulletSpeed);

            Vector position = new Vector(getBoundingBox().getCentre());

            Projectile projectile = new Projectile(position, speed);

            world.addBody(projectile, true);
        }
    }

    private Optional<AbstractMap.SimpleEntry<Vector, Double>> look() {
        return world.getLivingEntities().stream()
            .map(Living::getBoundingBox)
            .map(BoundingBox::getCentre)
            .map(Vector::new)
            .peek(vector -> vector.sub(this.getBoundingBox().getCentre()))
            .map(vector -> new AbstractMap.SimpleEntry<>(vector, vector.angle(this.speed)))
            .filter(entry -> entry.getValue() < 0.2)
            .min(Comparator.comparing(entry -> entry.getKey().lengthSquared()));
    }

    private void move() {
        acceleration.add(new Vector(random.nextDouble() - 0.5, 0));
        acceleration.scale(0.9);
        speed.add(acceleration);
    }

}
