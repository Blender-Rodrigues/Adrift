package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.graphics.Renderer;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;

import static ee.taltech.iti0200.application.Game.eventBus;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

import static ee.taltech.iti0200.graphics.Graphics.DEFAULT;

public class Player extends Living {

    private static final long serialVersionUID = 1L;

    private static final Vector SIZE = new Vector(1.0, 1.0);
    private static final double MASS = 70.0;
    private static final double ELASTICITY = 0.25;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final int JUMP_AMOUNT_LIMIT = 2;
    private static final double JUMP_DELTA_V = 10.0;
    private static final int MAX_HEALTH = 200;
    private static final int FIRE_RATE = 4;

    private int jumpsLeft;
    private Vector lookingAt;

    public Player(Vector position, World world) {
        super(MASS, new BoundingBox(position, SIZE), world, MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.jumpsLeft = JUMP_AMOUNT_LIMIT;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.gun = new Gun(boundingBox, FIRE_RATE, this);
        this.lookingAt = new Vector(1f, 0f);
    }

    public void shoot() {
        if (!gun.canShoot(world.getTime())) {
            return;
        }

        eventBus.dispatch(new GunShot(gun, lookingAt, SERVER));
    }

    public void setLookingAt(Vector targetPosition) {
        targetPosition.sub(boundingBox.getCentre());
        targetPosition.normalize();
        lookingAt = targetPosition;
        gun.setPointedAt(lookingAt);
    }

    public double getJumpDeltaV() {
        return JUMP_DELTA_V;
    }

    public void setJumpsLeft(int jumpsLeft) {
        this.jumpsLeft = jumpsLeft;
    }

    public int getJumpsLeft() {
        return this.jumpsLeft;
    }

    @Override
    public void render(Shader shader, Camera camera, long tick, Matrix4f rotation) {
        String renderer = jumpsLeft < JUMP_AMOUNT_LIMIT ? "jump" : DEFAULT;
        renderers.get(renderer).render(shader, camera, tick, rotation);
    }

    @Override
    public void onCollide(Body otherBody) {
        super.onCollide(otherBody);
        if (otherBody instanceof Terrain) {
            boolean verticalCollision = boundingBox.getOverLap(otherBody.getBoundingBox()).getY() == 0;
            boolean otherIsBelow = boundingBox.getCentre().getY() > otherBody.getBoundingBox().getCentre().getY();
            if (verticalCollision && otherIsBelow) {
                setJumpsLeft(JUMP_AMOUNT_LIMIT);
            }
        }
    }

}
