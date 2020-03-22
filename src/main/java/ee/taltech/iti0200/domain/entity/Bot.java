package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Animateable;
import ee.taltech.iti0200.graphics.Animation;
import ee.taltech.iti0200.graphics.Renderer;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

import javax.vecmath.Vector2d;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

public class Bot extends Living {

    private static final long serialVersionUID = 1L;

    private static final Vector SIZE = new Vector(1.5, 1.5);
    private static final double MASS = 70.0;
    private static final Random RANDOM = new Random();
    private static final double ELASTICITY = 0.25;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final int MAX_HEALTH = 100;
    private static final int FIRE_RATE = 90;

    private Vector acceleration;
    private Gun gun;

    public Bot(Vector position, World world) {
        super(MASS, new BoundingBox(position, SIZE), world, MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.acceleration = new Vector(0.0, 0.0);
        this.gun = new Gun(boundingBox, FIRE_RATE, this);
        this.movable = true;
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        if (tick % 10 == 0) {
            move();
            if (gun.canShoot(tick)) {
                lookForPlayer().ifPresent(target -> world.addEntity(gun.shoot(target, tick)));
            }
        }
    }

    private Optional<Vector> lookForPlayer() {
        return world.getLivingEntities().stream()
            .filter(Player.class::isInstance)
            .map(player -> {
                Vector vector = new Vector(player.getBoundingBox().getCentre());
                vector.sub(getBoundingBox().getCentre());
                return vector;
            })
            .filter(vector -> vector.angle(speed) < 0.2)
            .min(Comparator.comparing(Vector2d::lengthSquared));
    }

    private void move() {
        acceleration.add(new Vector(RANDOM.nextDouble() - 0.5, 0));
        acceleration.scale(0.9);
        speed.add(acceleration);
    }

}
